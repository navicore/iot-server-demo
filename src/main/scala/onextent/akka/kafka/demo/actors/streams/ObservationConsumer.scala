package onextent.akka.kafka.demo.actors.streams

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf
import onextent.akka.kafka.demo.actors.DeviceService.SetAssessment
import onextent.akka.kafka.demo.models.functions.JsonSupport
import onextent.akka.kafka.demo.models.{Assessment, Observation}
import spray.json._

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object ObservationConsumer extends LazyLogging with Conf with JsonSupport {

  def apply(deviceService: ActorRef)(
      implicit timeout: Timeout,
      ec: ExecutionContext): CommittableMessage[Array[Byte], String] => Future[
    (Observation, CommittableMessage[Array[Byte], String])] = {

    (msg: CommittableMessage[Array[Byte], String]) =>
      val promise = Promise[(Observation, CommittableMessage[Array[Byte], String])]()

      val observation: Observation =
        msg.record.value().parseJson.convertTo[Observation]

      val key = new String(
        Option(msg.record.key())
          .getOrElse(observation.deviceId.toString.getBytes("UTF8")))

      logger.debug(s"key: $key observation: $observation")

      val assessment = Assessment(observation.name, observation.value)
      logger.debug(s"assessment: $assessment")

      val f = deviceService ask SetAssessment(assessment, observation.deviceId)

      f onComplete {
        case Success(_) =>
          promise.success((observation, msg))
        case Failure(e) =>
          logger.warn(s"can not update device assessment $assessment: $e")
          promise.success((observation, msg))
      }

      promise.future
  }

}
