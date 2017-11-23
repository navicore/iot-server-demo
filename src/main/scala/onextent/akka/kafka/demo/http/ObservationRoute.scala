package onextent.akka.kafka.demo.http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.http.functions.{HttpSupport, KafkaProducerDirective}
import onextent.akka.kafka.demo.models._
import onextent.akka.kafka.demo.models.functions.JsonSupport
import spray.json._

object ObservationRoute
    extends JsonSupport
    with LazyLogging
    with Directives
    with KafkaProducerDirective
    with HttpSupport {

  def apply(): Route =
    path(urlpath / "observation") {
      post {
        decodeRequest {
          entity(as[ObservationRequest]) { observationReq =>
            val observation = MkObservation(observationReq)
            write(observation.toJson.prettyPrint, observation.deviceId.toString) { f =>
              onSuccess(f) {
                complete(StatusCodes.Accepted)
              }
            }
          }
        }
      }
    }

}
