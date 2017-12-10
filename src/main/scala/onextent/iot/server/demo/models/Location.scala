package onextent.iot.server.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

case class Location(name: String,
                    id: UUID = UUID.randomUUID(),
                    latitude: Double,
                    longitude: Double,
                    fleet: Option[UUID] = None,
                    created: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC))

object MkLocation {

  def apply(locationReq: LocationRequest): Location = {
    Location(locationReq.name,
             locationReq.id.getOrElse(UUID.randomUUID()),
             locationReq.latitude,
             locationReq.longitude,
             locationReq.fleet)
  }

}

final case class LocationRequest(name: String,
                                 latitude: Double,
                                 longitude: Double,
                                 id: Option[UUID],
                                 fleet: Option[UUID])
