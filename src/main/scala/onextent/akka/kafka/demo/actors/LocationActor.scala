package onextent.akka.kafka.demo.actors

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.LocationActor.{Get, GetAssessments}
import onextent.akka.kafka.demo.models.{Assessment, Location}

object LocationActor {
  def props(location: Location)(implicit timeout: Timeout) =
    Props(new LocationActor(location))
  final case class Get()
  final case class GetAssessments()
}

class LocationActor(location: Location) extends Actor with LazyLogging {

  def receive: Receive = hasState(List[Assessment]())

  def hasState(assessments: List[Assessment]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessment :: assessments)

    case Get =>
      sender() ! location

    case GetAssessments =>
      sender() ! assessments
  }

}
