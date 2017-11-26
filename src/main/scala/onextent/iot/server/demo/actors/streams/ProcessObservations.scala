package onextent.iot.server.demo.actors.streams

import java.util.UUID

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.kafka.{ProducerMessage, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.streams.functions._
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}
import org.apache.kafka.clients.producer.ProducerRecord
import onextent.iot.server.demo.models.functions.JsonSupport
import spray.json._

object ForwardableMessage extends LazyLogging with Conf {

  def apply[K,V](ev: (EnrichedAssessment[Device], CommittableMessage[K,V]))
    : ProducerMessage.Message[Array[Byte], String, CommittableOffset] = {

    val ea = ev._1
    val msg = ev._2

    ProducerMessage.Message(
      new ProducerRecord[Array[Byte], String](
        deviceAssessmentsTopic,
        ea.enrichment.location.toString.getBytes("UTF8"),
        ea.enrichment.toJson.prettyPrint
      ),
      msg.committableOffset
    )

  }
}

object ProcessObservations extends LazyLogging {

  def apply(deviceService: ActorRef, locationService: ActorRef)(
      implicit timeout: Timeout): Unit = {

    // read from Kafka and enrich

    val eventStream = Consumer
      .committableSource(consumerSettings, Subscriptions.topics(observationsTopic))
      .map(ExtractObservations())
      .mapAsync(parallelism) { EnrichWithDevice(deviceService) }
      .mapAsync(parallelism) { CommitKafkaOffset() }
      .mapConcat(FilterDevicesWithLocations())
      .map { ForwardableMessage(_) }
      .runWith(Producer.commitableSink(producerSettings))

    /*

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

     */
  }

}
