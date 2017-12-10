package onextent.iot.server.demo.actors.location

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import onextent.iot.server.demo.http.functions.HttpSupport

object ShardedLocationService {
  def props(fleetService: ActorRef) = Props(new ShardedLocationService(fleetService))
  def name = "shardedLocationService"
}

class ShardedLocationService(fleetService: ActorRef) extends Actor with HttpSupport {

  ClusterSharding(context.system).start(
    LocationService.shardName,
    LocationService.props(fleetService),
    ClusterShardingSettings(context.system),
    LocationService.extractEntityId,
    LocationService.extractShardId
  )

  def shardedLocationService: ActorRef = {
    ClusterSharding(context.system).shardRegion(LocationService.shardName)
  }

  override def receive: Receive = {
    case m =>
      shardedLocationService forward m
  }

}
