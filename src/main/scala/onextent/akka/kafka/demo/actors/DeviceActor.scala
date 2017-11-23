package onextent.akka.kafka.demo.actors

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.DeviceActor.{Get, GetAssessments}
import onextent.akka.kafka.demo.models.{Assessment, Device}

object DeviceActor {
  def props(device: Device)(implicit timeout: Timeout) =
    Props(new DeviceActor(device))
  final case class Get()
  final case class GetAssessments()
}

class DeviceActor(device: Device) extends Actor with LazyLogging {

  def receive: Receive = hasState(List[Assessment]())

  def hasState(assessments: List[Assessment]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessment :: assessments)

    case Get =>
      logger.debug("ejs got actor get")
      sender() ! device

    case GetAssessments =>
      logger.debug("ejs got actor get assessments")
      sender() ! assessments
  }

}
