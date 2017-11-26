package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import onextent.iot.server.demo.models.Device

/**
  * mapConcat helper to drop records that have no location
  */
object FilterDevicesWithLocations {

  def apply[T](): ((T, Device, _)) => List[(T, UUID)] = t => {
    t._2.location match {
      case Some(location) =>
        List((t._1, location))
      case _ =>
        List()
    }
  }
}
