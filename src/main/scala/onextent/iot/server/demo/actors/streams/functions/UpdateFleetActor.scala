package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.fleet.FleetActor.SetFleetAssessment
import onextent.iot.server.demo.models.Assessment
import onextent.iot.server.demo.models.functions.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

object UpdateFleetActor extends LazyLogging with Conf with JsonSupport {

  def apply(fleetService: ActorRef)(implicit timeout: Timeout,
                                    ec: ExecutionContext)
    : ((Assessment, UUID)) => Future[(Assessment, UUID)] = {
    case x @ (assessment, destId) =>
      (fleetService ask SetFleetAssessment(assessment, destId)).map(_ => x)
  }

}
