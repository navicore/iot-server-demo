package onextent.iot.server.demo.actors.fleet

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
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

class FleetActor(fleet: Fleet)
    extends Actor
    with LazyLogging
    with PersistentActor
    with Conf {

  logger.debug(s"fleet actor for '${fleet.name}' created")

  var state: (Map[String, Assessment], Map[UUID, Location], Map[UUID, Fleet]) =
    (Map(), Map(), Map())

  val snapShotInterval: Int = conf.getInt("main.snapShotInterval")

  override def persistenceId: String =
    conf.getString("main.fleetPersistenceId") + "_" + fleet.id.toString
      .replace('-', '_')

  private def takeSnapshot(): Unit =
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
      saveSnapshot(state)

  override def receiveRecover: Receive = {

    // BEGIN DB RECOVERY
    case assessment: Assessment =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments + (assessment.name -> assessment),
               curLocations,
               curFleets)

    case newLocation: Location =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments, curLocations + (newLocation.id -> newLocation), curFleets)

    case newFleet: Fleet =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments, curLocations, curFleets + (newFleet.id -> newFleet))

    case SnapshotOffer(
        _,
        snapshot: (Map[String, Assessment], Map[UUID, Location], Map[UUID, Fleet]) @unchecked) =>
      state = snapshot

    // END DB RECOVERY
  }

  override def receiveCommand: Receive = {

    case SetFleetAssessment(assessment, _) =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments + (assessment.name -> assessment),
               curLocations,
               curFleets)
      persistAsync(assessment) { _ =>
        sender() ! FleetAssessmentAck(fleet)
        takeSnapshot()
      }

    case AddFleetToFleet(newFleet, _) =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments, curLocations, curFleets + (newFleet.id -> newFleet))
      persistAsync(newFleet) { _ =>
        takeSnapshot()
      }

    case AddLocationToFleet(newLocation, _) =>
      val (curAssessments, curLocations, curFleets) = state
      state = (curAssessments, curLocations + (newLocation.id -> newLocation), curFleets)
      persistAsync(newLocation) { _ =>
        takeSnapshot()
      }

    case GetFleetFleets(_) =>
      val (_, _, curFleets) = state
      sender() ! curFleets.values.toList

    case GetFleetLocations(_) =>
      val (_, curLocations, _) = state
      sender() ! curLocations.values.toList

    case GetFleet(_) =>
      sender() ! fleet

    case GetFleetAssessments(_) =>
      val (curAssessments, _, _) = state
      sender() ! curAssessments.values.toList
  }

}
