package onextent.iot.server.demo.actors.fleet

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import onextent.iot.server.demo.http.functions.HttpSupport

object ShardedFleetService {
  def props = Props(new ShardedFleetService)
  def name = "shardedFleetService"
}

class ShardedFleetService extends Actor with HttpSupport {

  ClusterSharding(context.system).start(
    FleetService.shardName,
    FleetService.props,
    ClusterShardingSettings(context.system),
    FleetService.extractEntityId,
    FleetService.extractShardId
  )

  def shardedFleetService: ActorRef = {
    ClusterSharding(context.system).shardRegion(FleetService.shardName)
  }

  override def receive: Receive = {
    case m =>
      shardedFleetService forward m
  }

}
