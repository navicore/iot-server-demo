package onextent.akka.kafka.demo

import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf._
import onextent.akka.kafka.demo.actors.streams.Consume
import onextent.akka.kafka.demo.actors.{DeviceService, LocationService}
import onextent.akka.kafka.demo.http.{DeviceRoute, HttpSupport, LocationRoute}

object Main extends App with LazyLogging with HttpSupport with Directives {

  val deviceService: ActorRef =
    actorSystem.actorOf(DeviceService.props(timeout), DeviceService.name)

  val locationService: ActorRef =
    actorSystem.actorOf(LocationService.props(timeout), LocationService.name)

  Consume()

  val route =
    HealthCheck ~
      logRequest(urlpath) {
        handleErrors {
          cors(corsSettings) {
            DeviceRoute(deviceService) ~
              LocationRoute(locationService)
          }
        }
      }

  Http().bindAndHandle(route, "0.0.0.0", port)

}
