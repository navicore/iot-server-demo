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
import onextent.iot.server.demo.models.{Assessment, Device, EnrichedAssessment, Observation}

import scala.concurrent.{ExecutionContext, Future}

object EnrichWithDevice extends LazyLogging with Conf with JsonSupport {

  def apply[K, V](deviceService: ActorRef)(implicit timeout: Timeout,
                                           ec: ExecutionContext)
    : ((Observation, CommittableMessage[K, V])) => Future[
      (EnrichedAssessment[Device], CommittableMessage[K, V])] = {
    case (ob, msg) =>
      val assessment = Assessment(ob.name, ob.value)

      (deviceService ask SetAssessment(assessment, ob.deviceId)).map({
        case Ack(device) => (EnrichedAssessment(assessment, device), msg)
      })
  }

}
