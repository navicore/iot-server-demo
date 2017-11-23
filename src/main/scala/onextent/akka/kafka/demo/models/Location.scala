package onextent.akka.kafka.demo.models

import java.util.UUID

case class Location(name: String,
                    id: UUID = UUID.randomUUID(),
                    latitude: Double,
                    longitude: Double)

object MkLocation {

  def apply(locationReq: LocationRequest): Location = {
    Location(locationReq.name,
             locationReq.id.getOrElse(UUID.randomUUID()),
             locationReq.latitude,
             locationReq.longitude)
  }

}

final case class LocationRequest(name: String,
                                 latitude: Double,
                                 longitude: Double,
                                 id: Option[UUID])
