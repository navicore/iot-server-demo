package onextent.iot.server.demo.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.location.LocationActor._
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.models._
import onextent.iot.server.demo.models.functions.JsonSupport
import spray.json._

import scala.concurrent.Future

object LocationRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  private def lookupAssessments(service: ActorRef): Route =
    path(urlpath / "location" / JavaUUID / "assessments") { id =>
      get {
        val f: Future[Any] = service ask GetLocationAssessments(id)
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
  private def lookupDevices(service: ActorRef): Route =
    path(urlpath / "location" / JavaUUID / "devices") { id =>
      get {
        val f: Future[Any] = service ask GetLocationDevices(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case devices: List[Device @unchecked] =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             devices.toJson.prettyPrint))
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
  private def lookupLocation(service: ActorRef): Route =
    path(urlpath / "location" / JavaUUID) { id =>
      get {
        val f: Future[Any] = service ask GetLocation(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case location: Location =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                             location.toJson.prettyPrint))
              case _ =>
                complete(StatusCodes.NotFound)
            }
          }
        }
      }
    }
  private def create(service: ActorRef): Route =
    path(urlpath / "location") {
      post {
        decodeRequest {
          entity(as[LocationRequest]) { locationReq =>
            val f: Future[Any] = service ask CreateLocation(MkLocation(locationReq))
            onSuccess(f) { (r: Any) =>
              {
                r match {
                  case location: Location =>
                    complete(
                      HttpEntity(ContentTypes.`application/json`,
                                 location.toJson.prettyPrint))
                  case LocationAlreadyExists(d) =>
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
      lookupDevices(service) ~
      lookupLocation(service) ~
      create(service)
}
