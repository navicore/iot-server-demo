package onextent.akka.kafka.demo.actors.streams

import akka.actor.ActorRef
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf.{consumerSettings, parallelism, topic, _}
import onextent.akka.kafka.demo.actors.streams.windows._
import onextent.akka.kafka.demo.models.Observation

import scala.concurrent.Promise
import scala.util.{Failure, Success}

object ObservationPipeline extends LazyLogging {

  def apply(deviceService: ActorRef, locationService: ActorRef)(
      implicit timeout: Timeout): Unit = {

    val eventStream = Consumer
    // read bytes
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      // extract Observation and msg Actors
      .mapAsync(parallelism) {
        ObservationConsumer(deviceService)
      }
      // commit offset so we don't reread if we crash
      .mapAsync(parallelism) { t =>
        val promise = Promise[Observation]()
        t._2.committableOffset.commitScaladsl().onComplete {
          case Success(_) => promise.success(t._1)
          case Failure(e) =>
            logger.error(s"commit to kafka: $e")
            promise.failure(e)
        }
        promise.future
      }
      // process observations in windows
      .map { o =>
        logger.debug(s"ejs o is $o")
        o
      }

    val commandStream = eventStream.statefulMapConcat { () =>
      val generator = new CommandGenerator()
      ev =>
        generator.forEvent(ev)
    }

    val windowStreams = commandStream
      .groupBy(64, command => command.w)
      .takeWhile(!_.isInstanceOf[CloseWindow])
      .fold(AggregateEventData((0L, 0L), 0)) {
        case (agg, OpenWindow(window)) => agg.copy(w = window)
        // always filtered out by takeWhile
        case (agg, CloseWindow(_)) => agg
        case (agg, AddToWindow(_, _)) =>
          agg.copy(eventCount = agg.eventCount + 1)
      }
      .async

    //val aggregatesStream = windowStreams.mergeSubstreams
    windowStreams.mergeSubstreams
      .runForeach { agg =>
        logger.debug(s"agg: $agg")
      }
  }
}
