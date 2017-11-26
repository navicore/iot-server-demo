package onextent.iot.server.demo.actors.streams.functions

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.DeviceActor.Ack
import onextent.iot.server.demo.actors.DeviceService.SetAssessment
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models.{Assessment, Device, Observation}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object EnrichWithDevice extends LazyLogging with Conf with JsonSupport {

  def apply(deviceService: ActorRef)(implicit timeout: Timeout,
                                     ec: ExecutionContext): (
      (Observation, CommittableMessage[Array[Byte], String])) => Future[
    (Observation, Device, CommittableMessage[Array[Byte], String])] = {

    (x: (Observation, CommittableMessage[Array[Byte], String])) =>
      {
        val ob = x._1
        val msg = x._2

        val promise = Promise[(Observation,
                               Device,
                               CommittableMessage[Array[Byte], String])]()

        val assessment = Assessment(ob.name, ob.value)

        val f = deviceService ask SetAssessment(assessment, ob.deviceId)

        f onComplete {
          case Success(r: Any) =>
            r match {
              case Ack(device) =>
                promise.success((ob, device, msg))
              case (ack) =>
                promise.failure(new Exception(ack.toString))
            }
          case Failure(e) =>
            logger.warn(s"can not update device assessment $assessment: $e")
            promise.failure(e)
        }

        promise.future
      }
  }

}
