package onextent.iot.server.demo.models.functions

import java.io.ByteArrayOutputStream
import java.time.{ZoneOffset, ZonedDateTime}

import com.sksamuel.avro4s._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.device.DeviceActor.SetDeviceAssessment
import onextent.iot.server.demo.models.{Assessment, AvroSerializer}
import org.apache.avro.Schema
import org.apache.avro.Schema.Field

object AvroSupport extends JsonSupport {

  implicit object ZondedDateTimeToSchema extends ToSchema[ZonedDateTime] {
    override val schema: Schema = Schema.create(Schema.Type.STRING)
  }
  implicit object ZonedDateTimeToValue extends ToValue[ZonedDateTime] {
    override def apply(value: ZonedDateTime): String = value.toString
  }
  implicit object ZonedDateTimeFromValue extends FromValue[ZonedDateTime] {
    override def apply(value: Any, field: Field): ZonedDateTime =
      java.time.ZonedDateTime
        .ofInstant(parse8601(value.toString).toInstant, ZoneOffset.UTC)
  }

  class SetDeviceAssessmentSerializer
      extends AvroSerializer[SetDeviceAssessment]
      with JsonSupport
      with LazyLogging {

    override def identifier: Int = 100010

    final val Manifest = classOf[SetDeviceAssessment].getName

    override def toBinary(o: AnyRef): Array[Byte] = {

      logger.debug(s"ejs doing setdev avro to binary")

      implicit val toRec: ToRecord[SetDeviceAssessment] =
        ToRecord[SetDeviceAssessment]

      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[SetDeviceAssessment](output)
      avro.write(o.asInstanceOf[SetDeviceAssessment])
      avro.close()
      output.toByteArray

    }

    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {

      logger.debug(s"ejs doing setdev avro from binary")

      implicit val fromRec: FromRecord[SetDeviceAssessment] =
        FromRecord[SetDeviceAssessment]

      if (Manifest == manifest) {
        val is = AvroInputStream.binary[SetDeviceAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head

      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }

  }

  class AssessmentSerializer
      extends AvroSerializer[Assessment]
      with JsonSupport
      with LazyLogging {

    override def identifier: Int = 100010

    final val Manifest = classOf[Assessment].getName

    override def toBinary(o: AnyRef): Array[Byte] = {

      logger.debug(s"ejs doing avro to binary")

      implicit val toRec: ToRecord[Assessment] = ToRecord[Assessment]

      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[Assessment](output)
      avro.write(o.asInstanceOf[Assessment])
      avro.close()
      output.toByteArray

    }

    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {

      logger.debug(s"ejs doing avro from binary")
      implicit object ZonedDateTimeFromValue extends FromValue[ZonedDateTime] {
        override def apply(value: Any, field: Field): ZonedDateTime =
          java.time.ZonedDateTime
            .ofInstant(parse8601(value.toString).toInstant, ZoneOffset.UTC)
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

}
