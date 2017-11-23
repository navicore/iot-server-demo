package onextent.akka.kafka.demo.models

import java.util.UUID

case class Location(name: String,
                    latitude: Double,
                    longitude: Double,
                    id: UUID = UUID.randomUUID())

object MkLocation {

  def apply(locationReq: LocationRequest): Location = {
    val tmp =
      Location(locationReq.name, locationReq.latitude, locationReq.longitude)
    Location(tmp.name, tmp.latitude, tmp.longitude, tmp.id)
  }

}

final case class LocationRequest(name: String,
                                 latitude: Double,
                                 longitude: Double)
