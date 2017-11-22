package onextent.akka.kafka.demo.models

import java.util.UUID

final case class Device(name: String, id: UUID = UUID.randomUUID())
