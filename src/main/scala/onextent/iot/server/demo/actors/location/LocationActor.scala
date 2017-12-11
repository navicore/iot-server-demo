package onextent.iot.server.demo.actors.location

import java.util.UUID

import akka.actor.{Actor, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.location.LocationActor._
import onextent.iot.server.demo.models._

object LocationActor {
  def props(location: Location)(implicit timeout: Timeout) =
    Props(new LocationActor(location))

  final case class GetLocation(id: UUID)
  final case class GetLocationDevices(id: UUID)
  final case class GetLocationAssessments(id: UUID)
  final case class SetLocationAssessment(assessment: Assessment,
                                         locationId: UUID)
  final case class CreateLocation(location: Location)
  final case class LocationAlreadyExists(location: Location)
  final case class AddDeviceToLocation(device: Device, locationId: UUID)
  final case class LocationAssessmentAck(device: Location)

}

class LocationActor(location: Location)
    extends Actor
    with LazyLogging
    with PersistentActor
    with Conf {

  logger.debug(s"location actor for '${location.name}' created")

  val snapShotInterval: Int = conf.getInt("main.snapShotInterval")

  var state: (Map[String, Assessment], Map[UUID, Device]) = (Map(), Map())

  override def persistenceId: String =
    conf.getString("main.locationPersistenceId") + "_" + location.id.toString
      .replace('-', '_')

  private def takeSnapshot(): Unit =
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
      saveSnapshot(state)

  override def receiveRecover: Receive = {

    // BEGIN DB RECOVERY
    case assessment: Assessment =>
      val (curAssessments, curDevices) = state
      state = (curAssessments + (assessment.name -> assessment), curDevices)

    case device: Device =>
      val (curAssessments, curDevices) = state
      state = (curAssessments, curDevices + (device.id -> device))

    case SnapshotOffer(
        _,
        snapshot: (Map[String, Assessment], Map[UUID, Device]) @unchecked) =>
      state = snapshot
    // END DB RECOVERY
  }

  override def receiveCommand: Receive = {

    case assessment: Assessment =>
      val (curAssessments, curDevices) = state
      state = (curAssessments + (assessment.name -> assessment), curDevices)
      persistAsync(assessment) { _ =>
        sender() ! LocationAssessmentAck(location)
        takeSnapshot()
      }

    case AddDeviceToLocation(newDevice, _) =>
      val (curAssessments, curDevices) = state
      state = (curAssessments, curDevices + (newDevice.id -> newDevice))
      persistAsync(newDevice) { _ =>
        takeSnapshot()
      }

    case GetLocation(_) =>
      sender() ! location

    case GetLocationAssessments(_) =>
      val (curAssessments, _) = state
      sender() ! curAssessments.values.toList

    case GetLocationDevices(_) =>
      val (_, curDevices) = state
      sender() ! curDevices.values.toList
  }

}
