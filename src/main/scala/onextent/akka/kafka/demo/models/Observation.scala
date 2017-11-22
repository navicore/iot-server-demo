package onextent.akka.kafka.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Observation(name: String,
                             deviceId: UUID,
                             value: Double,
                             zonedDateTime: ZonedDateTime =
                               ZonedDateTime.now(ZoneOffset.UTC),
                             id: UUID = UUID.randomUUID())
