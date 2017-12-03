package onextent.iot.server.demo.actors.device

import java.util.UUID

import akka.actor._
import akka.cluster.sharding.ShardRegion
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.device.DeviceActor._
import onextent.iot.server.demo.actors.location.LocationActor.AddDeviceToLocation
import onextent.iot.server.demo.models.Device

object DeviceService extends Conf {

  def props(locationService: ActorRef)(implicit timeout: Timeout) =
    Props(new DeviceService(locationService))
  def shardName = "deviceService"

  def SHARDS: Int = deviceServiceShards

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case m: GetDevice            => (m.id.toString, m)
    case m: GetDeviceAssessments => (m.id.toString, m)
    case m: CreateDevice         => (m.device.id.toString, m)
    case m: SetDeviceAssessment  => (m.deviceId.toString, m)
  }

  def calcShardName(uuid: UUID): String = {
    s"${Math.abs(uuid.getLeastSignificantBits) % SHARDS}"
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case m: GetDevice            => calcShardName(m.id)
    case m: GetDeviceAssessments => calcShardName(m.id)
    case m: CreateDevice         => calcShardName(m.device.id)
    case m: SetDeviceAssessment  => calcShardName(m.deviceId)
  }

}

class DeviceService(locationService: ActorRef)(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  logger.debug(s"actor ${context.self.path} created")

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
