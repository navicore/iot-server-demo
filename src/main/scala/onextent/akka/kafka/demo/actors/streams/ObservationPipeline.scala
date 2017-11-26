package onextent.akka.kafka.demo.actors.streams

import java.util.UUID

import akka.actor.ActorRef
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf._
import onextent.akka.kafka.demo.actors.streams.functions._

object ObservationPipeline extends LazyLogging {

  def apply(deviceService: ActorRef, locationService: ActorRef)(
      implicit timeout: Timeout): Unit = {

    // read from Kafka and enrich

    val eventStream = Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .map(ExtractObservations())
      .mapAsync(parallelism) { EnrichWithDevice(deviceService) }
      .mapAsync(parallelism) { CommitKafkaOffset() }
      .mapConcat(FilterDevicesWithLocations())
      .recover {
        case e => throw e
      }
    // insert window open and close commands

    val commandStream = eventStream.statefulMapConcat { () =>
      val generator = new CommandGenerator()
      ev =>
        generator.forEvent(ev)
    }

    // keep windows by time, name, and enriched uuid (location or other grouping)

    val windowStreams = commandStream
      .groupBy(20000, command => command.w)
      .takeWhile(!_.isInstanceOf[CloseWindow])
      .fold(AggregateEventData((0L, 0L, "", UUID.randomUUID()))) {
        case (agg, OpenWindow(window)) => agg.copy(w = window)
        // always filtered out by takeWhile
        case (agg, CloseWindow(_)) => agg
        case (agg, AddToWindow(ev, _)) =>
          agg.copy(values = ev._1.value :: agg.values)
      }
      .async

    // convert to assessments and send them to location actors

    windowStreams.mergeSubstreams
      .mapConcat(MakeAssessments())
      .mapAsync(parallelism)(UpdateLocationActor(locationService))
      .runForeach { ev =>
        logger.debug(s"assessment: $ev")
      }

  }

}
