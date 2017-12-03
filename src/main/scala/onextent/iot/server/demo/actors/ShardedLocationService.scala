package onextent.iot.server.demo.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import onextent.iot.server.demo.http.functions.HttpSupport

object ShardedLocationService {
  def props = Props(new ShardedLocationService)
  def name = "shardedLocationService"
}

class ShardedLocationService extends Actor with HttpSupport {

  ClusterSharding(context.system).start(
    LocationService.shardName,
    LocationService.props,
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
