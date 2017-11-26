package onextent.iot.server.demo.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.LocationService._
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models._
import spray.json._

import scala.concurrent.Future

object LocationRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply(service: ActorRef): Route =
    path(urlpath / "location" / JavaUUID / "assessments") { id =>
      get {
        val f: Future[Any] = service ask GetAssessments(id)
        onSuccess(f) { (r: Any) =>
          {
            r match {
              case assessments: List[Assessment @unchecked] =>
                complete(HttpEntity(ContentTypes.`application/json`,
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
    } ~
      path(urlpath / "location" / JavaUUID / "devices") { id =>
        get {
          val f: Future[Any] = service ask GetDevices(id)
          onSuccess(f) { (r: Any) =>
            {
              r match {
                case devices: List[Device @unchecked] =>
                  complete(HttpEntity(ContentTypes.`application/json`,
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
      } ~
      path(urlpath / "location" / JavaUUID) { id =>
        get {
          val f: Future[Any] = service ask Get(id)
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
      } ~
      path(urlpath / "location") {
        post {
          decodeRequest {
            entity(as[LocationRequest]) { locationReq =>
              val f: Future[Any] = service ask Create(MkLocation(locationReq))
              onSuccess(f) { (r: Any) =>
                {
                  r match {
                    case location: Location =>
                      complete(HttpEntity(ContentTypes.`application/json`,
                                          location.toJson.prettyPrint))
                    case AlreadyExists(d) =>
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

}
