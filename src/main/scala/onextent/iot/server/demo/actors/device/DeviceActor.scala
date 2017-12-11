package onextent.iot.server.demo.actors.device

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.device.DeviceActor._
import onextent.iot.server.demo.models.{Assessment, Device}

object DeviceActor {
  def props(device: Device)(implicit timeout: Timeout) =
    Props(new DeviceActor(device))
  final case class DeviceAssessmentAck(device: Device)
  final case class GetDevice(id: UUID)
  final case class CreateDevice(device: Device)
  final case class GetDeviceAssessments(id: UUID)
  final case class SetDeviceAssessment(assessment: Assessment, deviceId: UUID)
  final case class DeviceAlreadyExists(device: Device)
}

class DeviceActor(device: Device) extends Actor with LazyLogging with Conf with PersistentActor {

  logger.debug(s"device actor for '${device.name}' created")

  val snapShotInterval: Int = conf.getInt("main.snapShotInterval")

  var state: Map[String, Assessment] = Map()

  override def persistenceId: String =
    conf.getString("main.devicePersistenceId") + "_" + device.id.toString.replace('-', '_')

  private def takeSnapshot(): Unit =
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
      saveSnapshot(state)

  override def receiveRecover: Receive = {

    // BEGIN DB RECOVERY
    case assessment: Assessment =>
      val curAssessments = state
      state = curAssessments + (assessment.name -> assessment)

    case SnapshotOffer(
        _,
        snapshot: Map[String, Assessment] @unchecked) =>
      state = snapshot
    // END DB RECOVERY
  }

  override def receiveCommand: Receive = {

    case assessment: Assessment =>
      val curAssessments = state
      state = curAssessments + (assessment.name -> assessment)
      persistAsync(assessment) { _ =>
        sender() ! DeviceAssessmentAck(device)
        takeSnapshot()
      }

    case GetDevice(_) =>
      sender() ! device

    case GetDeviceAssessments(_) =>
      val curAssessments = state
      sender() ! curAssessments.values.toList
  }

}
