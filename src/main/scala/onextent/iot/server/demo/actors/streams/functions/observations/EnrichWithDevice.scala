package onextent.iot.server.demo.actors.streams.functions.observations

import akka.actor.ActorRef
import akka.kafka.ConsumerMessage._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.DeviceActor.Ack
import onextent.iot.server.demo.actors.DeviceService.SetAssessment
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models.{Assessment, Device, EnrichedAssessment, Observation}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object EnrichWithDevice extends LazyLogging with Conf with JsonSupport {

  def apply[K, V](deviceService: ActorRef)(implicit timeout: Timeout,
                                           ec: ExecutionContext)
    : ((Observation, CommittableMessage[K, V])) => Future[
      (EnrichedAssessment[Device], CommittableMessage[K, V])] = {

    (x: (Observation, CommittableMessage[K, V])) =>
      {
        val ob = x._1
        val msg = x._2

        val promise =
          Promise[(EnrichedAssessment[Device], CommittableMessage[K, V])]()

        val assessment = Assessment(ob.name, ob.value)

        val f = deviceService ask SetAssessment(assessment, ob.deviceId)

        f onComplete {
          case Success(r: Any) =>
            r match {
              case Ack(device) =>
                val enriched = EnrichedAssessment(assessment, device)
                promise.success((enriched, msg))
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
