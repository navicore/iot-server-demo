package onextent.iot.server.demo.actors

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.DeviceActor._
import onextent.iot.server.demo.actors.LocationActor.AddDeviceToLocation
import onextent.iot.server.demo.models.Device

object DeviceService {
  def props(locationService: ActorRef)(implicit timeout: Timeout) =
    Props(new DeviceService(locationService))
  def name = "deviceService"


}

class DeviceService(locationService: ActorRef)(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  def create(actorId: String, device: Device): Unit = {
    context.actorOf(DeviceActor.props(device), actorId)
    sender() ! device
    device.location match {
      case Some(locationId) =>
        locationService ! AddDeviceToLocation(device, locationId)
      case _ =>
    }
  }

  override def receive: PartialFunction[Any, Unit] = {

    case SetDeviceAssessment(assessment, deviceId) =>
      def notFound(): Unit = logger.warn(s"device $deviceId not found for assessment $assessment update")
      context.child(deviceId.toString).fold(notFound())(_ forward assessment)

    case GetDevice(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetDevice(id))

    case GetDeviceAssessments(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetDeviceAssessments(id))

    case CreateDevice(device) =>
      context
        .child(device.id.toString)
        .fold(create(device.id.toString, device))(_ => {
          sender() ! DeviceAlreadyExists(device)
        })

  }

}
