package onextent.akka.kafka.demo.actors

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.LocationActor.{Ack, Get, GetAssessments, GetDevices}
import onextent.akka.kafka.demo.actors.LocationService.AddDevice
import onextent.akka.kafka.demo.models.{Assessment, Device, Location}

object LocationActor {
  def props(location: Location)(implicit timeout: Timeout) =
    Props(new LocationActor(location))
  final case class Get()
  final case class GetAssessments()
  final case class GetDevices()
  final case class Ack(device: Location)
}

class LocationActor(location: Location) extends Actor with LazyLogging {

  def receive: Receive = hasState(Map[String, Assessment](), List[Device]())

  def hasState(assessments: Map[String, Assessment], devices: List[Device]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessments + (assessment.name -> assessment), devices)
      sender() ! Ack(location)

    case AddDevice(device) =>
      context become hasState(assessments, device :: devices)

    case Get =>
      sender() ! location

    case GetAssessments =>
      sender() ! assessments.values.toList

    case GetDevices =>
      sender() ! devices
  }

}
