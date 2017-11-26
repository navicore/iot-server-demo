package onextent.iot.server.demo.actors

import java.util.UUID

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.DeviceService._
import onextent.iot.server.demo.actors.LocationService.AddDevice
import onextent.iot.server.demo.models.{Assessment, Device}

object DeviceService {
  def props(locationService: ActorRef)(implicit timeout: Timeout) =
    Props(new DeviceService(locationService))
  def name = "deviceService"

  final case class Get(id: UUID)
  final case class Create(device: Device)
  final case class GetAssessments(id: UUID)
  final case class SetAssessment(assessment: Assessment, deviceId: UUID)
  final case class AlreadyExists(device: Device)
}

class DeviceService(locationService: ActorRef)(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  def create(actorId: String, device: Device): Unit = {
    context.actorOf(DeviceActor.props(device), actorId)
    sender() ! device
    locationService ! AddDevice(device)
  }

  override def receive: PartialFunction[Any, Unit] = {

    case SetAssessment(assessment, deviceId) =>
      def notFound(): Unit = logger.warn(s"device $deviceId not found for assessment $assessment update")
      context.child(deviceId.toString).fold(notFound())(_ forward assessment)

    case Get(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward DeviceActor.Get)

    case GetAssessments(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward DeviceActor.GetAssessments)

    case Create(device) =>
      context
        .child(device.id.toString)
        .fold(create(device.id.toString, device))(_ => {
          sender() ! AlreadyExists(device)
        })

  }

}
