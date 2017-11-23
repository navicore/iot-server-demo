package onextent.akka.kafka.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Observation(name: String,
                             deviceId: UUID,
                             value: Double,
                             datetime: ZonedDateTime =
                               ZonedDateTime.now(ZoneOffset.UTC),
                             id: UUID = UUID.randomUUID())


object MkObservation {

  def apply(req: ObservationRequest): Observation = {
    Observation(req.name, req.deviceId, req.value)
  }

}

final case class ObservationRequest(name: String, deviceId: UUID, value: Double)
