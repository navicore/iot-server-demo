package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.location.LocationActor.{LocationAssessmentAck, SetLocationAssessment}
import onextent.iot.server.demo.models.Assessment
import onextent.iot.server.demo.models.functions.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

object UpdateLocationActor extends LazyLogging with Conf with JsonSupport {

  def apply(locationService: ActorRef)(implicit timeout: Timeout,
                                       ec: ExecutionContext)
    : ((Assessment, UUID)) => Future[(Assessment, UUID)] =
    {
      case _@(assessment, locationId) =>
        (locationService ask SetLocationAssessment(assessment, locationId)).map {
            case LocationAssessmentAck(location) if location.fleet.isDefined => (assessment, location.fleet.get) //ejs todo: don't do this
        }
    }

}
