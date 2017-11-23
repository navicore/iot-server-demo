package onextent.akka.kafka.demo.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.LocationService._
import onextent.akka.kafka.demo.models.functions.JsonSupport
import onextent.akka.kafka.demo.models.{Location, LocationRequest, MkLocation}
import spray.json._

import scala.concurrent.Future

object LocationRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with HttpSupport {

  def apply(service: ActorRef): Route =
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
