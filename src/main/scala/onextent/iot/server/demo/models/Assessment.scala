package onextent.iot.server.demo.models

import java.io.ByteArrayOutputStream
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

import com.sksamuel.avro4s._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.models.functions.JsonSupport
import org.apache.avro.Schema.Field

final case class Assessment(name: String, value: Double, datetime: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC), id: UUID = UUID.randomUUID())

final case class EnrichedAssessment[E](assessment: Assessment, enrichment: E)

import akka.serialization.SerializerWithStringManifest

abstract class AvroSerializer[T] extends SerializerWithStringManifest {

  override def manifest(o: AnyRef): String = o.getClass.getName

}

class AssessmentSerializer extends AvroSerializer[Assessment] with JsonSupport with LazyLogging {

  override def identifier: Int = 100010

  final val Manifest = classOf[Assessment].getName

  override def toBinary(o: AnyRef): Array[Byte] = {

    logger.debug(s"ejs doing avro to binary")
    throw new java.lang.IllegalAccessError("ha ha in")
    val output = new ByteArrayOutputStream
    val avro = AvroOutputStream.binary[Assessment](output)
    avro.write(o.asInstanceOf[Assessment])
    avro.close()
    output.toByteArray

  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {

    logger.debug(s"ejs doing avro from binary")
    throw new java.lang.IllegalAccessError("ha ha out")
    implicit object ZonedDateTimeFromValue extends FromValue[ZonedDateTime] {
      override def apply(value: Any, field: Field): ZonedDateTime =
        java.time.ZonedDateTime.ofInstant(parse8601(value.toString).toInstant, ZoneOffset.UTC)
    }
    implicit val fromRec: FromRecord[Assessment] = FromRecord[Assessment]

    if (Manifest == manifest) {
      val is = AvroInputStream.binary[Assessment](bytes)
      val events = is.iterator.toList
      is.close()
      events.head

    } else
      throw new IllegalArgumentException(
        s"Unable to handle manifest $manifest, required $Manifest")
  }

}
