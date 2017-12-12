package onextent.iot.server.demo

import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf._
import onextent.iot.server.demo.actors.device.{DeviceService, ShardedDeviceService}
import onextent.iot.server.demo.actors.fleet.{FleetService, ShardedFleetService}
import onextent.iot.server.demo.actors.location.{LocationService, ShardedLocationService}
import onextent.iot.server.demo.actors.streams._
import onextent.iot.server.demo.http.functions.HttpSupport
import onextent.iot.server.demo.http.{DeviceRoute, FleetRoute, LocationRoute, ObservationRoute}

object Main extends App with LazyLogging with HttpSupport with Directives {

  val fleetService: ActorRef =
  actorSystem.actorOf(ShardedFleetService.props, ShardedFleetService.name)

  val locationService: ActorRef =
  actorSystem.actorOf(ShardedLocationService.props(fleetService), ShardedLocationService.name)

  val deviceService: ActorRef =
  actorSystem.actorOf(ShardedDeviceService.props(locationService), ShardedDeviceService.name)

  if (isStreamer) {
    logger.info(s"streamer node.  starting singleton stream ingestors.")
    ProcessObservations(deviceService)
    ProcessDeviceAssessments(locationService)
    ProcessLocationAssessments(fleetService)
  }

  val route =
    HealthCheck ~
      logRequest(urlpath) {
        handleErrors {
          cors(corsSettings) {
            DeviceRoute(deviceService) ~
              LocationRoute(locationService) ~
              FleetRoute(fleetService) ~
              ObservationRoute()
          }
        }
      }

  Http().bindAndHandle(route, "0.0.0.0", port)

}
