package onextent.akka.kafka.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

case class Assessment(name: String,
                      value: Double,
                      datetime: ZonedDateTime =
                        ZonedDateTime.now(ZoneOffset.UTC),
                      id: UUID = UUID.randomUUID())
