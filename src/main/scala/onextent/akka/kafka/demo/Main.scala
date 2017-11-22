package onextent.akka.kafka.demo

import com.typesafe.scalalogging.LazyLogging
import Conf._
import akka.http.scaladsl.Http
import onextent.akka.kafka.demo.actors.streams.Consume
import onextent.akka.kafka.demo.http.HttpSupport

object Main extends App with LazyLogging with HttpSupport{

  Consume()

  val route =
    HealthCheck ~
      DemoRoute()

  Http().bindAndHandle(route, "0.0.0.0", port)

}
