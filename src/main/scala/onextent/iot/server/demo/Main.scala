package onextent.iot.server.demo

import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.{DeviceService, LocationService}
import onextent.iot.server.demo.actors.streams.ProcessObservations
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.http.{DeviceRoute, LocationRoute, ObservationRoute}
import onextent.iot.server.demo.Conf._

object Main extends App with LazyLogging with HttpSupport with Directives {

  val locationService: ActorRef =
    actorSystem.actorOf(LocationService.props, LocationService.name)

  val deviceService: ActorRef =
    actorSystem.actorOf(DeviceService.props(locationService), DeviceService.name)

  ProcessObservations(deviceService)

  val route =
    HealthCheck ~
      logRequest(urlpath) {
        handleErrors {
          cors(corsSettings) {
            DeviceRoute(deviceService) ~
            LocationRoute(locationService) ~
            ObservationRoute()
          }
        }
      }

  Http().bindAndHandle(route, "0.0.0.0", port)

}
