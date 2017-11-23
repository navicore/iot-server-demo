package onextent.akka.kafka.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Device(name: String,
                        id: UUID = UUID.randomUUID(),
                        created: ZonedDateTime =
                          ZonedDateTime.now(ZoneOffset.UTC),
                        kind: Option[Int] = None,
                        desc: Option[String] = None,
                        serial: Option[String] = None)

object MkDevice {

  def apply(deviceReq: DeviceRequest): Device = {
    val tmp = Device(deviceReq.name)
    Device(tmp.name,
           tmp.id,
           tmp.created,
           deviceReq.kind,
           deviceReq.desc,
           deviceReq.serial)
  }

}

final case class DeviceRequest(name: String,
                        kind: Option[Int],
                        desc: Option[String],
                        serial: Option[String])
