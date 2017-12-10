package onextent.iot.server.demo.models.functions

import java.io.ByteArrayOutputStream
import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Date

import akka.serialization.SerializerWithStringManifest
import com.sksamuel.avro4s._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.device.DeviceActor._
import onextent.iot.server.demo.actors.fleet.FleetActor._
import onextent.iot.server.demo.actors.location.LocationActor._
import onextent.iot.server.demo.models.{Device, Fleet, Location}
import org.apache.avro.Schema
import org.apache.avro.Schema.Field

// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
// ejs todo: get rid of all this dupe code. can't use normal generics due to avro4s macros not expanding parameterized types
object AvroSupport extends JsonSupport with LazyLogging {

  implicit object ZondedDateTimeToSchema extends ToSchema[ZonedDateTime] {
    override val schema: Schema = Schema.create(Schema.Type.STRING)
  }
  implicit object ZonedDateTimeToValue extends ToValue[ZonedDateTime] {
    override def apply(value: ZonedDateTime): String =
      get8601(new Date(value.toInstant.toEpochMilli))
  }
  implicit object ZonedDateTimeFromValue extends FromValue[ZonedDateTime] {
    override def apply(value: Any, field: Field): ZonedDateTime =
      java.time.ZonedDateTime
        .ofInstant(parse8601(value.toString).toInstant, ZoneOffset.UTC)
  }

  abstract class AvroSerializer[T] extends SerializerWithStringManifest {

    override def manifest(o: AnyRef): String = o.getClass.getName

  }

  class SetDeviceAssessmentSerializer
      extends AvroSerializer[SetDeviceAssessment] {
    override def identifier: Int = 100010
    final val Manifest = classOf[SetDeviceAssessment].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[SetDeviceAssessment](output)
      avro.write(o.asInstanceOf[SetDeviceAssessment])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
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
  class AddDeviceToLocationSerializer
      extends AvroSerializer[AddDeviceToLocation] {
    override def identifier: Int = 100011
    final val Manifest = classOf[AddDeviceToLocation].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[AddDeviceToLocation](output)
      avro.write(o.asInstanceOf[AddDeviceToLocation])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[AddDeviceToLocation] =
        FromRecord[AddDeviceToLocation]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[AddDeviceToLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class CreateDeviceSerializer extends AvroSerializer[CreateDevice] {
    override def identifier: Int = 100012
    final val Manifest = classOf[CreateDevice].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[CreateDevice](output)
      avro.write(o.asInstanceOf[CreateDevice])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[CreateDevice] =
        FromRecord[CreateDevice]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[CreateDevice](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class CreateLocationSerializer extends AvroSerializer[CreateLocation] {
    override def identifier: Int = 100013
    final val Manifest = classOf[CreateLocation].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[CreateLocation](output)
      avro.write(o.asInstanceOf[CreateLocation])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[CreateLocation] =
        FromRecord[CreateLocation]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[CreateLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class DeviceAlreadyExistsSerializer
      extends AvroSerializer[DeviceAlreadyExists] {
    override def identifier: Int = 100014
    final val Manifest = classOf[DeviceAlreadyExists].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[DeviceAlreadyExists](output)
      avro.write(o.asInstanceOf[DeviceAlreadyExists])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[DeviceAlreadyExists] =
        FromRecord[DeviceAlreadyExists]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[DeviceAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class DeviceAssessmentAckSerializer
      extends AvroSerializer[DeviceAssessmentAck] {
    override def identifier: Int = 100015
    final val Manifest = classOf[DeviceAssessmentAck].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[DeviceAssessmentAck](output)
      avro.write(o.asInstanceOf[DeviceAssessmentAck])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[DeviceAssessmentAck] =
        FromRecord[DeviceAssessmentAck]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[DeviceAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetDeviceSerializer extends AvroSerializer[GetDevice] {
    override def identifier: Int = 100016
    final val Manifest = classOf[GetDevice].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetDevice](output)
      avro.write(o.asInstanceOf[GetDevice])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetDevice] =
        FromRecord[GetDevice]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetDevice](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetDeviceAssessmentsSerializer
      extends AvroSerializer[GetDeviceAssessments] {
    override def identifier: Int = 100017
    final val Manifest = classOf[GetDeviceAssessments].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetDeviceAssessments](output)
      avro.write(o.asInstanceOf[GetDeviceAssessments])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetDeviceAssessments] =
        FromRecord[GetDeviceAssessments]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetDeviceAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetLocationSerializer extends AvroSerializer[GetLocation] {
    override def identifier: Int = 100018
    final val Manifest = classOf[GetLocation].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetLocation](output)
      avro.write(o.asInstanceOf[GetLocation])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetLocation] =
        FromRecord[GetLocation]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetLocationAssessmentsSerializer
      extends AvroSerializer[GetLocationAssessments] {
    override def identifier: Int = 100019
    final val Manifest = classOf[GetLocationAssessments].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetLocationAssessments](output)
      avro.write(o.asInstanceOf[GetLocationAssessments])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetLocationAssessments] =
        FromRecord[GetLocationAssessments]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetLocationAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetLocationDevicesSerializer
      extends AvroSerializer[GetLocationDevices] {
    override def identifier: Int = 100020
    final val Manifest = classOf[GetLocationDevices].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetLocationDevices](output)
      avro.write(o.asInstanceOf[GetLocationDevices])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetLocationDevices] =
        FromRecord[GetLocationDevices]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetLocationDevices](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class LocationAlreadyExistsSerializer
      extends AvroSerializer[LocationAlreadyExists] {
    override def identifier: Int = 100021
    final val Manifest = classOf[LocationAlreadyExists].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[LocationAlreadyExists](output)
      avro.write(o.asInstanceOf[LocationAlreadyExists])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[LocationAlreadyExists] =
        FromRecord[LocationAlreadyExists]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[LocationAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class LocationAssessmentAckSerializer
      extends AvroSerializer[LocationAssessmentAck] {
    override def identifier: Int = 100022
    final val Manifest = classOf[LocationAssessmentAck].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[LocationAssessmentAck](output)
      avro.write(o.asInstanceOf[LocationAssessmentAck])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[LocationAssessmentAck] =
        FromRecord[LocationAssessmentAck]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[LocationAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class SetLocationAssessmentSerializer
      extends AvroSerializer[SetLocationAssessment] {
    override def identifier: Int = 100023
    final val Manifest = classOf[SetLocationAssessment].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[SetLocationAssessment](output)
      avro.write(o.asInstanceOf[SetLocationAssessment])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[SetLocationAssessment] =
        FromRecord[SetLocationAssessment]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[SetLocationAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class DeviceSerializer extends AvroSerializer[Device] {
    override def identifier: Int = 100024
    final val Manifest = classOf[Device].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[Device](output)
      avro.write(o.asInstanceOf[Device])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[Device] =
        FromRecord[Device]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[Device](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class LocationSerializer extends AvroSerializer[Location] {
    override def identifier: Int = 100025
    final val Manifest = classOf[Location].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[Location](output)
      avro.write(o.asInstanceOf[Location])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[Location] =
        FromRecord[Location]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[Location](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class FleetSerializer extends AvroSerializer[Fleet] {
    override def identifier: Int = 100026
    final val Manifest = classOf[Fleet].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[Fleet](output)
      avro.write(o.asInstanceOf[Fleet])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[Fleet] =
        FromRecord[Fleet]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[Fleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetFleetSerializer extends AvroSerializer[GetFleet] {
    override def identifier: Int = 100026
    final val Manifest = classOf[GetFleet].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetFleet](output)
      avro.write(o.asInstanceOf[GetFleet])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetFleet] =
        FromRecord[GetFleet]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetFleetLocationsSerializer extends AvroSerializer[GetFleetLocations] {
    override def identifier: Int = 100026
    final val Manifest = classOf[GetFleetLocations].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetFleetLocations](output)
      avro.write(o.asInstanceOf[GetFleetLocations])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetFleetLocations] =
        FromRecord[GetFleetLocations]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetFleetLocations](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class AddLocationToFleetSerializer extends AvroSerializer[AddLocationToFleet] {
    override def identifier: Int = 100026
    final val Manifest = classOf[AddLocationToFleet].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[AddLocationToFleet](output)
      avro.write(o.asInstanceOf[AddLocationToFleet])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[AddLocationToFleet] =
        FromRecord[AddLocationToFleet]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[AddLocationToFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetFleetFleetsSerializer extends AvroSerializer[GetFleetFleets] {
    override def identifier: Int = 100026
    final val Manifest = classOf[GetFleetFleets].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetFleetFleets](output)
      avro.write(o.asInstanceOf[GetFleetFleets])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetFleetFleets] =
        FromRecord[GetFleetFleets]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetFleetFleets](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class AddFleetToFleetSerializer extends AvroSerializer[AddFleetToFleet] {
    override def identifier: Int = 100026
    final val Manifest = classOf[AddFleetToFleet].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[AddFleetToFleet](output)
      avro.write(o.asInstanceOf[AddFleetToFleet])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[AddFleetToFleet] =
        FromRecord[AddFleetToFleet]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[AddFleetToFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class GetFleetAssessmentsSerializer extends AvroSerializer[GetFleetAssessments] {
    override def identifier: Int = 100026
    final val Manifest = classOf[GetFleetAssessments].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[GetFleetAssessments](output)
      avro.write(o.asInstanceOf[GetFleetAssessments])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[GetFleetAssessments] =
        FromRecord[GetFleetAssessments]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[GetFleetAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class SetFleetAssessmentSerializer extends AvroSerializer[SetFleetAssessment] {
    override def identifier: Int = 100026
    final val Manifest = classOf[SetFleetAssessment].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[SetFleetAssessment](output)
      avro.write(o.asInstanceOf[SetFleetAssessment])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[SetFleetAssessment] =
        FromRecord[SetFleetAssessment]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[SetFleetAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class CreateFleetSerializer extends AvroSerializer[CreateFleet] {
    override def identifier: Int = 100026
    final val Manifest = classOf[CreateFleet].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[CreateFleet](output)
      avro.write(o.asInstanceOf[CreateFleet])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[CreateFleet] =
        FromRecord[CreateFleet]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[CreateFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class FleetAlreadyExistsSerializer extends AvroSerializer[FleetAlreadyExists] {
    override def identifier: Int = 100026
    final val Manifest = classOf[FleetAlreadyExists].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[FleetAlreadyExists](output)
      avro.write(o.asInstanceOf[FleetAlreadyExists])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[FleetAlreadyExists] =
        FromRecord[FleetAlreadyExists]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[FleetAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
  class FleetAssessmentAckSerializer extends AvroSerializer[FleetAssessmentAck] {
    override def identifier: Int = 100026
    final val Manifest = classOf[FleetAssessmentAck].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[FleetAssessmentAck](output)
      avro.write(o.asInstanceOf[FleetAssessmentAck])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[FleetAssessmentAck] =
        FromRecord[FleetAssessmentAck]
      if (Manifest == manifest) {
        val is = AvroInputStream.binary[FleetAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $Manifest")
    }
  }
}
