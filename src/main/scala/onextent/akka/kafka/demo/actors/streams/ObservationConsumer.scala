package onextent.akka.kafka.demo.actors.streams

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf
import onextent.akka.kafka.demo.actors.DeviceService.SetAssessment
import onextent.akka.kafka.demo.models.functions.JsonSupport
import onextent.akka.kafka.demo.models.{Assessment, Observation}
import spray.json._

import scala.concurrent.{Future, Promise}

object ObservationConsumer extends LazyLogging with Conf with JsonSupport {

  def apply(deviceService: ActorRef)
    : CommittableMessage[Array[Byte], String] => Future[
      CommittableMessage[Array[Byte], String]] = {

    (msg: CommittableMessage[Array[Byte], String]) =>
      val promise = Promise[CommittableMessage[Array[Byte], String]]()

      val observation: Observation = msg.record.value().parseJson.convertTo[Observation]

      val key = new String(
        Option(msg.record.key())
          .getOrElse(observation.deviceId.toString.getBytes("UTF8")))

      logger.debug(s"key: $key observation: $observation")

      val assessment = Assessment(observation.name, observation.value)
      logger.debug(s"assessment: $assessment")

      deviceService ! SetAssessment(assessment, observation.deviceId)

      promise.success(msg) // note, not async yet, need an ack from the device actor to affect back pressure

      promise.future
  }

}
