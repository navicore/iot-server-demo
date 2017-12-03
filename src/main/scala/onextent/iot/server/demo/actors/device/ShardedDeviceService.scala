package onextent.iot.server.demo.actors.device

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import onextent.iot.server.demo.http.functions.HttpSupport


object ShardedDeviceService {
  def props(locationService: ActorRef) = Props(new ShardedDeviceService(locationService))
  def name = "shardedDeviceService"
}

class ShardedDeviceService(locationService: ActorRef) extends Actor with HttpSupport {

  ClusterSharding(context.system).start(
    DeviceService.shardName,
    DeviceService.props(locationService),
    ClusterShardingSettings(context.system),
    DeviceService.extractEntityId,
    DeviceService.extractShardId
  )

  def shardedDeviceService: ActorRef = {
    ClusterSharding(context.system).shardRegion(DeviceService.shardName)
  }

  override def receive: Receive = {
    case m =>
      shardedDeviceService forward m
  }

}
