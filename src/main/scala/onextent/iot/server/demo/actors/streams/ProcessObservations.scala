package onextent.iot.server.demo.actors.streams

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ProducerMessage, Subscriptions}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.streams.functions.observations.{EnrichWithDevice, ExtractObservations, FilterDevicesWithLocations}
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json._

private object ForwardableMessage
    extends LazyLogging
    with Conf
    with JsonSupport {

  def apply[K, V](ev: (EnrichedAssessment[Device], CommittableMessage[K, V]))
    : ProducerMessage.Message[Array[Byte], String, CommittableOffset] = {

    ProducerMessage.Message(
      new ProducerRecord[Array[Byte], String](
        deviceAssessmentsTopic,
        ev._1.enrichment.location.toString.getBytes("UTF8"),
        ev._1.toJson.prettyPrint
      ),
      ev._2.committableOffset
    )

  }
}

object ProcessObservations extends LazyLogging {

  def apply(deviceService: ActorRef)(implicit timeout: Timeout): Unit = {

    // read, enrich, and publish

    Consumer
      .committableSource(consumerSettings,
                         Subscriptions.topics(observationsTopic))
      .map(ExtractObservations())
      .mapAsync(parallelism) { EnrichWithDevice(deviceService) }
      .mapConcat(FilterDevicesWithLocations())
      .map { ForwardableMessage(_) }
      .runWith(Producer.commitableSink(producerSettings))

  }

}
