package onextent.iot.server.demo.actors.fleet

import java.util.UUID

import akka.actor._
import akka.cluster.sharding.ShardRegion
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.actors.fleet.FleetActor._
import onextent.iot.server.demo.models.Fleet

object FleetService extends Conf {

  def props(implicit timeout: Timeout) = Props(new FleetService)
  def shardName = "fleetService"

  def SHARDS: Int = fleetServiceShards

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case m: GetFleet            => (m.id.toString, m)
    case m: GetFleetAssessments => (m.id.toString, m)
    case m: CreateFleet         => (m.fleet.id.toString, m)
    case m: SetFleetAssessment  => (m.fleetId.toString, m)
    case m: AddLocationToFleet  => (m.fleetId.toString, m)
    case m: GetFleetLocations   => (m.id.toString, m)
    case m: AddFleetToFleet     => (m.fleetId.toString, m)
    case m: GetFleetFleets      => (m.id.toString, m)
  }

  def calcShardName(uuid: UUID): String = {
    s"${Math.abs(uuid.getLeastSignificantBits) % SHARDS}"
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case m: GetFleet            => calcShardName(m.id)
    case m: GetFleetAssessments => calcShardName(m.id)
    case m: CreateFleet         => calcShardName(m.fleet.id)
    case m: SetFleetAssessment  => calcShardName(m.fleetId)
    case m: AddLocationToFleet  => calcShardName(m.fleetId)
    case m: GetFleetLocations   => calcShardName(m.id)
    case m: AddFleetToFleet     => calcShardName(m.fleetId)
    case m: GetFleetFleets      => calcShardName(m.id)
  }
}

class FleetService(implicit timeout: Timeout) extends Actor with LazyLogging {

  logger.debug(s"actor ${context.self.path} created")

  def create(actorId: String, fleet: Fleet): Unit = {
    context.actorOf(FleetActor.props(fleet), actorId)
    sender() ! fleet
    fleet.fleet match {
      case Some(fleetId) =>
        self ! AddFleetToFleet(fleet, fleetId)
      case _ =>
    }
  }

  override def receive: PartialFunction[Any, Unit] = {

    case GetFleet(id) =>
      def notFound(): Unit = sender() ! None
      context.child(id.toString).fold(notFound())(_ forward GetFleet(id))

    case CreateFleet(fleet) =>
      context
        .child(fleet.id.toString)
        .fold(create(fleet.id.toString, fleet))(_ => {
          sender() ! FleetAlreadyExists(fleet)
        })

    case GetFleetAssessments(id) =>
      def notFound(): Unit = sender() ! None
      context
        .child(id.toString)
        .fold(notFound())(_ forward GetFleetAssessments(id))

    case SetFleetAssessment(assessment, fleetId) =>
      def notFound(): Unit =
        logger.warn(
          s"fleet $fleetId not found for assessment $assessment update")
      context.child(fleetId.toString).fold(notFound())(_ forward SetFleetAssessment(assessment, fleetId))

    case AddLocationToFleet(location, fleetId) =>
      context
        .child(fleetId.toString)
        .fold(logger.warn(s"fleet not found: $fleetId"))(
          _ forward AddLocationToFleet(location, fleetId))

    case GetFleetLocations(id) =>
      def notFound(): Unit = sender() ! None
      context
        .child(id.toString)
        .fold(notFound())(_ forward GetFleetLocations(id))

    case AddFleetToFleet(fleet, fleetId) =>
      context
        .child(fleetId.toString)
        .fold(logger.warn(s"fleet not found: $fleetId"))(
          _ forward AddFleetToFleet(fleet, fleetId))

    case GetFleetFleets(id) =>
      def notFound(): Unit = sender() ! None
      context
        .child(id.toString)
        .fold(notFound())(_ forward GetFleetFleets(id))

  }

}
