package onextent.iot.server.demo.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.fleet.FleetActor._
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.models._
import onextent.iot.server.demo.models.functions.JsonSupport
import spray.json._

import scala.concurrent.Future

object FleetRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  private def lookupAssessments(service: ActorRef): Route =
    path(urlpath / "fleet" / JavaUUID / "assessments") { id =>
      get {
        val f: Future[Any] = service ask GetFleetAssessments(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case assessments: List[Assessment @unchecked] =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             assessments.toJson.prettyPrint))
              case None =>
                complete(StatusCodes.NotFound)
              case e =>
                logger.error(s"$e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  private def lookupFleets(service: ActorRef): Route =
    path(urlpath / "fleet" / JavaUUID / "fleets") { id =>
      get {
        val f: Future[Any] = service ask GetFleetFleets(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case fleets: List[Fleet @unchecked] =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             fleets.toJson.prettyPrint))
              case None =>
                complete(StatusCodes.NotFound)
              case e =>
                logger.error(s"$e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  private def lookupLocations(service: ActorRef): Route =
    path(urlpath / "fleet" / JavaUUID / "locations") { id =>
      get {
        val f: Future[Any] = service ask GetFleetLocations(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case fleets: List[Location @unchecked] =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             fleets.toJson.prettyPrint))
              case None =>
                complete(StatusCodes.NotFound)
              case e =>
                logger.error(s"$e")
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
    }
  private def lookupFleet(service: ActorRef): Route =
    path(urlpath / "fleet" / JavaUUID) { id =>
      get {
        val f: Future[Any] = service ask GetFleet(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case fleet: Fleet =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             fleet.toJson.prettyPrint))
              case _ =>
                complete(StatusCodes.NotFound)
            }
          }
        }
      }
    }
  private def create(service: ActorRef): Route =
    path(urlpath / "fleet") {
      post {
        decodeRequest {
          entity(as[FleetRequest]) { fleetReq =>
            val f: Future[Any] = service ask CreateFleet(MkFleet(fleetReq))
            onSuccess(f) { (r: Any) =>
              {
                r match {
                  case fleet: Fleet =>
                    complete(
                      HttpEntity(ContentTypes.`application/json`,
                                 fleet.toJson.prettyPrint))
                  case FleetAlreadyExists(d) =>
                    complete(StatusCodes.Conflict, s"${d.id} already exists")
                  case _ =>
                    complete(StatusCodes.NotFound)
                }
              }
            }
          }
        }
      }
    }

  def apply(service: ActorRef): Route =
    lookupAssessments(service) ~
      lookupLocations(service) ~
      lookupFleets(service) ~
      lookupFleet(service) ~
      create(service)
}
