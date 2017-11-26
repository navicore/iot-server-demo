package onextent.iot.server.demo.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.DeviceService.{AlreadyExists, Create, Get, GetAssessments}
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models.{Assessment, Device, DeviceRequest, MkDevice}
import spray.json._

object DeviceRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply(service: ActorRef): Route =
    path(urlpath / "device" / JavaUUID / "assessments") { id =>
      get {
        val f = service ask GetAssessments(id)
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
    } ~
      path(urlpath / "device" / JavaUUID) { id =>
        get {
          val f = service ask Get(id)
          onSuccess(f) { (r: Any) =>
            {
              r match {
                case device: Device =>
                  complete(
                    HttpEntity(ContentTypes.`application/json`,
                               device.toJson.prettyPrint))
                case _ =>
                  complete(StatusCodes.NotFound)
              }
            }
          }
        }
      } ~
      path(urlpath / "device") {
        post {
          decodeRequest {
            entity(as[DeviceRequest]) { deviceReq =>
              val f = service ask Create(MkDevice(deviceReq))
              onSuccess(f) { (r: Any) =>
                {
                  r match {
                    case device: Device =>
                      complete(HttpEntity(ContentTypes.`application/json`,
                                          device.toJson.prettyPrint))
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
