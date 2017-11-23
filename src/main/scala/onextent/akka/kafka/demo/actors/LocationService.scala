package onextent.akka.kafka.demo.actors

import java.util.UUID

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.LocationService._
import onextent.akka.kafka.demo.models.Location

object LocationService {
  def props(implicit timeout: Timeout) = Props(new LocationService)
  def name = "locationService"

  final case class Get(id: UUID)
  final case class Create(location: Location)
  final case class AlreadyExists(location: Location)
}

class LocationService(implicit timeout: Timeout) extends Actor with LazyLogging {

  def create(actorId: String, location: Location): Unit = {
    context.actorOf(LocationActor.props(location), actorId)
    sender() ! location
  }

  override def receive: PartialFunction[Any, Unit] = {

    case Get(id) =>
      logger.debug(s"ejs got one $id")
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward LocationActor.Get)

    case Create(location) =>
      context
        .child(location.id.toString)
        .fold(create(location.id.toString, location))(_ => {
          sender() ! AlreadyExists(location)
        })

  }

}
