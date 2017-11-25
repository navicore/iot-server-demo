package onextent.akka.kafka.demo.actors.streams

import java.util.UUID

import onextent.akka.kafka.demo.models.Device

object FilterDevicesWithLocations {

  // mapConcat helper (needs a better name or should encapsulate mapConcat)
  def apply[T](): ((T, Device, _)) => List[(T, UUID)] = t => {
    t._2.location match {
      case Some(location) =>
        List((t._1, location))
      case _ =>
        List()
    }
  }
}
