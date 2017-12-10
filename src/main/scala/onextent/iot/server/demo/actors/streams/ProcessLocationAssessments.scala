package onextent.iot.server.demo.actors.streams

import java.util.UUID

import akka.actor.ActorRef
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.streams.functions._
import onextent.iot.server.demo.models.Assessment

object ProcessLocationAssessments extends LazyLogging {

  def apply(fleetService: ActorRef)(implicit timeout: Timeout): Unit = {

    // read from Kafka and commit offsets for now - todo: coordinate offsets with WindowClose

    val eventStream = Consumer
      .committableSource(consumerSettings,
                         Subscriptions.topics(locationAssessmentsTopic))
      .map(ExtractAssessments())
      .mapAsync(parallelism)(CommitKafkaOffset()) //todo: integrate with batches of committable offsets per window

    // insert window open and close commands

    val commandStream = eventStream.statefulMapConcat { () =>
      val generator = new AssessmentCommandGenerator()
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
            case (holder: (Assessment, UUID) @unchecked, _) =>
              holder._1.value :: agg.values
            case _ => throw new IllegalStateException()
          })
      }
      .async

    // convert to assessments and send them to location actors

    windowStreams.mergeSubstreams
      .mapConcat(AggregatesToAssessments())
      .mapAsync(parallelism)(UpdateFleetActor(fleetService))
      // write to kafka for downstream rollup
      .map { AssessmentProducerMessage(_) }
      .runWith(Producer.plainSink(producerSettings))

  }

}
