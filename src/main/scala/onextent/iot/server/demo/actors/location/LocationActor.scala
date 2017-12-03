package onextent.iot.server.demo.actors.location

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.location.LocationActor._
import onextent.iot.server.demo.models._

object LocationActor {
  def props(location: Location)(implicit timeout: Timeout) =
    Props(new LocationActor(location))

  final case class GetLocation(id: UUID)
  final case class GetLocationDevices(id: UUID)
  final case class GetLocationAssessments(id: UUID)
  final case class SetLocationAssessment(assessment: Assessment, locationId: UUID)
  final case class CreateLocation(location: Location)
  final case class LocationAlreadyExists(location: Location)
  final case class AddDeviceToLocation(device: Device, locationId: UUID)
  final case class LocationAssessmentAck(device: Location)

}

class LocationActor(location: Location) extends Actor with LazyLogging {

  def receive: Receive = hasState(Map[String, Assessment](), List[Device]())

  def hasState(assessments: Map[String, Assessment], devices: List[Device]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessments + (assessment.name -> assessment), devices)
      sender() ! LocationAssessmentAck(location)

    case AddDeviceToLocation(device, _) =>
      context become hasState(assessments, device :: devices)

    case GetLocation(_) =>
      sender() ! location

    case GetLocationAssessments(_) =>
      sender() ! assessments.values.toList

    case GetLocationDevices(_) =>
      sender() ! devices
  }

}
