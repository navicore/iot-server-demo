package onextent.iot.server.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Fleet(name: String,
                       id: UUID = UUID.randomUUID(),
                       created: ZonedDateTime =
                         ZonedDateTime.now(ZoneOffset.UTC),
                       fleet: Option[UUID] = None)

object MkFleet {

  def apply(fleetReq: FleetRequest): Fleet = {
    val tmp = Fleet(fleetReq.name, fleetReq.id.getOrElse(UUID.randomUUID()))
    Fleet(tmp.name, tmp.id, tmp.created, fleetReq.fleet)
  }

}

final case class FleetRequest(name: String,
                              id: Option[UUID],
                              fleet: Option[UUID])
