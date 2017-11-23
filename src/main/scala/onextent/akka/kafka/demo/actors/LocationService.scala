package onextent.akka.kafka.demo.actors

import java.util.UUID

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.LocationService._
import onextent.akka.kafka.demo.models.{Device, Location}

object LocationService {
  def props(implicit timeout: Timeout) = Props(new LocationService)
  def name = "locationService"

  final case class Get(id: UUID)
  final case class GetDevices(id: UUID)
  final case class GetAssessments(id: UUID)
  final case class Create(location: Location)
  final case class AlreadyExists(location: Location)
  final case class AddDevice(device: Device)
}

class LocationService(implicit timeout: Timeout) extends Actor with LazyLogging {

  def create(actorId: String, location: Location): Unit = {
    context.actorOf(LocationActor.props(location), actorId)
    sender() ! location
  }

  override def receive: PartialFunction[Any, Unit] = {

    case Get(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward LocationActor.Get)

    case GetAssessments(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward LocationActor.GetAssessments)

    case GetDevices(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward LocationActor.GetDevices)

    case Create(location) =>
      context
        .child(location.id.toString)
        .fold(create(location.id.toString, location))(_ => {
          sender() ! AlreadyExists(location)
        })

    case AddDevice(device) =>
      device.location match {
        case Some(l) =>
          context
            .child(l.toString)
            .fold(logger.warn(s"location not found: $device"))(_ forward AddDevice(device))
        case _ => logger.warn(s"no location field: $device")
      }

  }

}
