package onextent.iot.server.demo

import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.device. ShardedDeviceService
import onextent.iot.server.demo.actors.location.ShardedLocationService
import onextent.iot.server.demo.actors.streams._
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.http.{DeviceRoute, LocationRoute, ObservationRoute}

object Main extends App with LazyLogging with HttpSupport with Directives {

  val locationService: ActorRef =
    actorSystem.actorOf(ShardedLocationService.props,
                        ShardedLocationService.name)

  val deviceService: ActorRef =
    actorSystem.actorOf(ShardedDeviceService.props(locationService),
                        ShardedDeviceService.name)


  if (isSeed) {
    logger.info(s"seed node.  starting singleton stream ingestors.")
    ProcessObservations(deviceService)
    ProcessDeviceAssessments(locationService)
  }

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
