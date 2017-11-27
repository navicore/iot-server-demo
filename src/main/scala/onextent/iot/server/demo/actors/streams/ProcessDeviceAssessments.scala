package onextent.iot.server.demo.actors.streams

import java.util.UUID

import akka.actor.ActorRef
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.streams.functions._

object ProcessDeviceAssessments extends LazyLogging {

  def apply(locationService: ActorRef)(implicit timeout: Timeout): Unit = {

    // read from Kafka and commit offsets for now - todo: coordinate offsets with WindowClose

    val eventStream = Consumer
      .committableSource(consumerSettings,
                         Subscriptions.topics(deviceAssessmentsTopic))
      .map(ExtractDeviceAssessments())
      .mapAsync(parallelism)(CommitKafkaOffset()) //todo: integrate with CloseWindow

    // insert window open and close commands

    val commandStream = eventStream.statefulMapConcat { () =>
      val generator = new CommandGenerator()
      ev =>
        generator.forEvent(ev)
    }

    // keep windows by time, name, and enriched uuid (location or other grouping)

    val windowStreams = commandStream
      .groupBy(maxWindows, command => command.w)
      .takeWhile(!_.isInstanceOf[CloseWindow]) //todo: somehow integrate the close command with the batch of commits to avoid reading part of a window on restart
      .fold(AggregateEventData((0L, 0L, "", UUID.randomUUID()))) {
        case (agg, OpenWindow(window)) => agg.copy(w = window)
        // always filtered out by takeWhile
        case (agg, CloseWindow(_)) => agg
        case (agg, AddToWindow(ev, _)) =>
          agg.copy(values = ev match {
            case (holder, _) => holder.assessment.value :: agg.values
          })
      }
      .async

    // convert to assessments and send them to location actors

    windowStreams.mergeSubstreams
      .mapConcat(AggregatesToAssessments())
      .mapAsync(parallelism)(UpdateLocationActor(locationService))
      .runForeach { ev =>
        logger.debug(s"assessment: $ev")
      }

  }

}
