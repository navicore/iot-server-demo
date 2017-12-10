package onextent.iot.server.demo.actors.fleet

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.fleet.FleetActor._
import onextent.iot.server.demo.models._

object FleetActor {
  def props(fleet: Fleet)(implicit timeout: Timeout) =
    Props(new FleetActor(fleet))

  final case class GetFleet(id: UUID)

  final case class GetFleetLocations(id: UUID)
  final case class AddLocationToFleet(device: Location, fleetId: UUID)

  final case class GetFleetFleets(id: UUID)
  final case class AddFleetToFleet(fleet: Fleet, fleetId: UUID)

  final case class GetFleetAssessments(id: UUID)
  final case class SetFleetAssessment(assessment: Assessment, fleetId: UUID)
  final case class CreateFleet(fleet: Fleet)
  final case class FleetAlreadyExists(fleet: Fleet)
  final case class FleetAssessmentAck(device: Fleet)

}

class FleetActor(fleet: Fleet) extends Actor with LazyLogging {

  logger.debug(s"fleet actor for '${fleet.name}' created")

  def receive: Receive = hasState(Map(), List(), List())

  def hasState(assessments: Map[String, Assessment], locations: List[Location], fleets: List[Fleet]): Receive = {

    case assessment: Assessment =>
      context become hasState(assessments + (assessment.name -> assessment), locations, fleets)
      sender() ! FleetAssessmentAck(fleet)

    case AddFleetToFleet(newFleet, _) =>
      context become hasState(assessments, locations, newFleet :: fleets)

    case GetFleetFleets(_) =>
      sender() ! fleets

    case AddLocationToFleet(newLocation, _) =>
      context become hasState(assessments, newLocation :: locations, fleets)

    case GetFleetLocations(_) =>
      sender() ! locations

    case GetFleet(_) =>
      sender() ! fleet

    case GetFleetAssessments(_) =>
      sender() ! assessments.values.toList
  }

}
