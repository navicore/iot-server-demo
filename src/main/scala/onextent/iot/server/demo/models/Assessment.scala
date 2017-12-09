package onextent.iot.server.demo.models

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

final case class Assessment(name: String,
                            value: Double,
                            datetime: ZonedDateTime =
                              ZonedDateTime.now(ZoneOffset.UTC),
                            id: UUID = UUID.randomUUID())

final case class EnrichedAssessment[E](assessment: Assessment, enrichment: E)

import akka.serialization.SerializerWithStringManifest


