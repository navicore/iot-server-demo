package onextent.akka.kafka.demo.actors

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.DeviceService.Get
import onextent.akka.kafka.demo.models.Device

object DeviceService {
  def props(implicit timeout: Timeout) = Props(new DeviceService)
  def name = "deviceService"

  final case class Get(name: String)
  final case class Create(device: Device)
}

class DeviceService(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  override def receive: PartialFunction[Any, Unit] = {
    case Get(name) =>
      //def notFound(): Unit = sender() ! None
      //context.child(name).fold(notFound())(_ forward Holder.Get())
  }

}
