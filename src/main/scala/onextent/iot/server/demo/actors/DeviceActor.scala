package onextent.iot.server.demo.actors

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.DeviceActor._
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

class DeviceActor(device: Device) extends Actor with LazyLogging {

  def receive: Receive = hasState(Map[String, Assessment]())

  def hasState(assessments: Map[String, Assessment]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessments + (assessment.name -> assessment))
      sender() ! DeviceAssessmentAck(device)

    case GetDevice(_) =>
      sender() ! device

    case GetDeviceAssessments(_) =>
      sender() ! assessments.values.toList
  }

}
