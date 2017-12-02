package onextent.iot.server.demo.actors

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.models.Location
import onextent.iot.server.demo.actors.LocationActor._

object LocationService {
  def props(implicit timeout: Timeout) = Props(new LocationService)
  def name = "locationService"


}

class LocationService(implicit timeout: Timeout) extends Actor with LazyLogging {

  def create(actorId: String, location: Location): Unit = {
    context.actorOf(LocationActor.props(location), actorId)
    sender() ! location
  }

  override def receive: PartialFunction[Any, Unit] = {

    case GetLocation(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetLocation(id))

    case GetLocationAssessments(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetLocationAssessments(id))

    case GetLocationDevices(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetLocationDevices(id))

    case CreateLocation(location) =>
      context
        .child(location.id.toString)
        .fold(create(location.id.toString, location))(_ => {
          sender() ! LocationAlreadyExists(location)
        })

    case SetLocationAssessment(assessment, locationId) =>
      def notFound(): Unit = logger.warn(s"location $locationId not found for assessment $assessment update")
      context.child(locationId.toString).fold(notFound())(_ forward assessment)

    case AddDeviceToLocation(device) =>
      device.location match {
        case Some(l) =>
          context
            .child(l.toString)
            .fold(logger.warn(s"location not found: $device"))(_ forward AddDeviceToLocation(device))
        case _ => logger.warn(s"no location field: $device")
      }

  }

}
