package onextent.iot.server.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Device(name: String,
                        id: UUID = UUID.randomUUID(),
                        created: ZonedDateTime =
                          ZonedDateTime.now(ZoneOffset.UTC),
                        location: Option[UUID] = None,
                        kind: Option[Int] = None,
                        desc: Option[String] = None,
                        serial: Option[String] = None)

object MkDevice {

  def apply(deviceReq: DeviceRequest): Device = {
    val tmp = Device(deviceReq.name, deviceReq.id.getOrElse(UUID.randomUUID()))
    Device(tmp.name,
           tmp.id,
           tmp.created,
           deviceReq.location,
           deviceReq.kind,
           deviceReq.desc,
           deviceReq.serial)
  }

}

final case class DeviceRequest(name: String,
                               id: Option[UUID],
                               location: Option[UUID],
                               kind: Option[Int],
                               desc: Option[String],
                               serial: Option[String])
