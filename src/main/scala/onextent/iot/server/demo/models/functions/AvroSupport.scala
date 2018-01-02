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
import onextent.iot.server.demo.models.{Assessment, Device, Fleet, Location}
import org.apache.avro.Schema
import org.apache.avro.Schema.Field

import scala.reflect.ClassTag

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
    final val maniFest = classOf[SetDeviceAssessment].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[SetDeviceAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class AddDeviceToLocationSerializer
      extends AvroSerializer[AddDeviceToLocation] {
    override def identifier: Int = 100011
    final val maniFest = classOf[AddDeviceToLocation].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[AddDeviceToLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class CreateDeviceSerializer extends AvroSerializer[CreateDevice] {
    override def identifier: Int = 100012
    final val maniFest = classOf[CreateDevice].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[CreateDevice](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class CreateLocationSerializer extends AvroSerializer[CreateLocation] {
    override def identifier: Int = 100013
    final val maniFest = classOf[CreateLocation].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[CreateLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class DeviceAlreadyExistsSerializer
      extends AvroSerializer[DeviceAlreadyExists] {
    override def identifier: Int = 100014
    final val maniFest = classOf[DeviceAlreadyExists].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[DeviceAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class DeviceAssessmentAckSerializer
      extends AvroSerializer[DeviceAssessmentAck] {
    override def identifier: Int = 100015
    final val maniFest = classOf[DeviceAssessmentAck].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[DeviceAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetDeviceSerializer extends AvroSerializer[GetDevice] {
    override def identifier: Int = 100016
    final val maniFest = classOf[GetDevice].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetDevice](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetDeviceAssessmentsSerializer
      extends AvroSerializer[GetDeviceAssessments] {
    override def identifier: Int = 100017
    final val maniFest = classOf[GetDeviceAssessments].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetDeviceAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetLocationSerializer extends AvroSerializer[GetLocation] {
    override def identifier: Int = 100018
    final val maniFest = classOf[GetLocation].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetLocation](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetLocationAssessmentsSerializer
      extends AvroSerializer[GetLocationAssessments] {
    override def identifier: Int = 100019
    final val maniFest = classOf[GetLocationAssessments].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetLocationAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetLocationDevicesSerializer
      extends AvroSerializer[GetLocationDevices] {
    override def identifier: Int = 100020
    final val maniFest = classOf[GetLocationDevices].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetLocationDevices](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class LocationAlreadyExistsSerializer
      extends AvroSerializer[LocationAlreadyExists] {
    override def identifier: Int = 100021
    final val maniFest = classOf[LocationAlreadyExists].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[LocationAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class LocationAssessmentAckSerializer
      extends AvroSerializer[LocationAssessmentAck] {
    override def identifier: Int = 100022
    final val maniFest = classOf[LocationAssessmentAck].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[LocationAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class SetLocationAssessmentSerializer
      extends AvroSerializer[SetLocationAssessment] {
    override def identifier: Int = 100023
    final val maniFest = classOf[SetLocationAssessment].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[SetLocationAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class DeviceSerializer extends AvroSerializer[Device] {
    override def identifier: Int = 100024
    final val maniFest = classOf[Device].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[Device](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class LocationSerializer extends AvroSerializer[Location] {
    override def identifier: Int = 100025
    final val maniFest = classOf[Location].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[Location](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class FleetSerializer extends AvroSerializer[Fleet] {
    override def identifier: Int = 100026
    final val maniFest = classOf[Fleet].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[Fleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetFleetSerializer extends AvroSerializer[GetFleet] {
    override def identifier: Int = 100027
    final val maniFest = classOf[GetFleet].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetFleetLocationsSerializer extends AvroSerializer[GetFleetLocations] {
    override def identifier: Int = 100028
    final val maniFest = classOf[GetFleetLocations].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetFleetLocations](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class AddLocationToFleetSerializer extends AvroSerializer[AddLocationToFleet] {
    override def identifier: Int = 100029
    final val maniFest = classOf[AddLocationToFleet].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[AddLocationToFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetFleetFleetsSerializer extends AvroSerializer[GetFleetFleets] {
    override def identifier: Int = 100030
    final val maniFest = classOf[GetFleetFleets].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetFleetFleets](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class AddFleetToFleetSerializer extends AvroSerializer[AddFleetToFleet] {
    override def identifier: Int = 100031
    final val maniFest = classOf[AddFleetToFleet].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[AddFleetToFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class GetFleetAssessmentsSerializer extends AvroSerializer[GetFleetAssessments] {
    override def identifier: Int = 100032
    final val maniFest = classOf[GetFleetAssessments].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[GetFleetAssessments](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class SetFleetAssessmentSerializer extends AvroSerializer[SetFleetAssessment] {
    override def identifier: Int = 100033
    final val maniFest = classOf[SetFleetAssessment].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[SetFleetAssessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class CreateFleetSerializer extends AvroSerializer[CreateFleet] {
    override def identifier: Int = 100034
    final val maniFest = classOf[CreateFleet].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[CreateFleet](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class FleetAlreadyExistsSerializer extends AvroSerializer[FleetAlreadyExists] {
    override def identifier: Int = 100035
    final val maniFest = classOf[FleetAlreadyExists].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[FleetAlreadyExists](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }
  class FleetAssessmentAckSerializer extends AvroSerializer[FleetAssessmentAck] {
    override def identifier: Int = 100036
    final val maniFest = classOf[FleetAssessmentAck].getName
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
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[FleetAssessmentAck](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }

  // import scala.reflect.runtime.universe._
  // //abstract class CustomSerializer[T]()(implicit ct: ClassTag[T], wt: WeakTypeTag[T]) extends AvroSerializer[T] {
  // abstract class CustomSerializer[T]()(implicit ct: ClassTag[T], wt: WeakTypeTag[T]) extends AvroSerializer[T] {
  //   final val maniFest = ct.runtimeClass.getName
  //   implicit val schema: SchemaFor[T] = SchemaFor[T]
  //   override def toBinary(o: AnyRef): Array[Byte] = {
  //     val output = new ByteArrayOutputStream
  //     val avro = AvroOutputStream.binary[T](output)
  //     avro.write(o.asInstanceOf[T])
  //     avro.close()
  //     output.toByteArray
  //   }
  //   override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
  //     implicit val fromRec: FromRecord[T] =
  //       FromRecord[T]
  //     if (maniFest == manifest) {
  //       val is = AvroInputStream.binary[T](bytes)
  //       val events: Seq[T] = is.iterator.toList
  //       is.close()
  //       events.head
  //     } else
  //       throw new IllegalArgumentException(
  //         s"Unable to handle manifest $manifest, required $maniFest")
  //   }
  // }
  //
  // class AssessmentSerializer extends CustomSerializer[Assessment] {
  //   override def identifier: Int = 100040
  // }
  class AssessmentSerializer extends AvroSerializer[Assessment] {
    override def identifier: Int = 100037
    final val maniFest = classOf[Assessment].getName
    override def toBinary(o: AnyRef): Array[Byte] = {
      val output = new ByteArrayOutputStream
      val avro = AvroOutputStream.binary[Assessment](output)
      avro.write(o.asInstanceOf[Assessment])
      avro.close()
      output.toByteArray
    }
    override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
      implicit val fromRec: FromRecord[Assessment] =
        FromRecord[Assessment]
      if (maniFest == manifest) {
        val is = AvroInputStream.binary[Assessment](bytes)
        val events = is.iterator.toList
        is.close()
        events.head
      } else
        throw new IllegalArgumentException(
          s"Unable to handle manifest $manifest, required $maniFest")
    }
  }

}
