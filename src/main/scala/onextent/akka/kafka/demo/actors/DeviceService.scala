package onextent.akka.kafka.demo.actors

import java.util.UUID

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.DeviceService._
import onextent.akka.kafka.demo.actors.LocationService.AddDevice
import onextent.akka.kafka.demo.models.Device

object DeviceService {
  def props(locationService: ActorRef)(implicit timeout: Timeout) =
    Props(new DeviceService(locationService))
  def name = "deviceService"

  final case class Get(id: UUID)
  final case class Create(device: Device)
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

    case Get(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward DeviceActor.Get)

    case Create(device) =>
      context
        .child(device.id.toString)
        .fold(create(device.id.toString, device))(_ => {
          sender() ! AlreadyExists(device)
        })

  }

}
