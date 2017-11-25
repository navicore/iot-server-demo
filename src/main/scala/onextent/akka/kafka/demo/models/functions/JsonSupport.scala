package onextent.akka.kafka.demo.models.functions

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import onextent.akka.kafka.demo.models._
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX",
    java.util.Locale.US)
  dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"))

  def parse8601(dateString: String): java.util.Date = {
    dateFormat.parse(dateString)
  }
  def get8601(date: java.util.Date): String = {
    dateFormat.format(date)
  }
  def now8601(): String = {
    val now = new java.util.Date()
    get8601(now)
  }

  implicit object ZonedDateTime extends JsonFormat[ZonedDateTime] {
    def write(dt: ZonedDateTime): JsValue = JsString(dt.toString) //ejs not formatting right
    def read(value: JsValue): ZonedDateTime = {
      value match {
        case JsString(dt) => java.time.ZonedDateTime.ofInstant(parse8601(dt).toInstant, ZoneOffset.UTC)
        case _            => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit object Date extends JsonFormat[Date] {
    def write(dt: java.util.Date): JsValue = JsString(get8601(dt))
    def read(value: JsValue): java.util.Date = {
      value match {
        case JsString(dt) => parse8601(dt)
        case _            => throw DeserializationException("Expected 8601")
      }
    }
  }

  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(uuid: UUID): JsValue = JsString(uuid.toString)
    def read(value: JsValue): UUID = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _ =>
          throw DeserializationException("Expected hexadecimal UUID string")
      }
    }
  }

  implicit val assessmentFormat: RootJsonFormat[Assessment] = jsonFormat4(
    Assessment)

  implicit val locationFormat: RootJsonFormat[Location] = jsonFormat5(
    Location)

  implicit val locationReqFormat: RootJsonFormat[LocationRequest] = jsonFormat4(LocationRequest)

  implicit val deviceFormat: RootJsonFormat[Device] = jsonFormat7(Device)

  implicit val deviceReqFormat: RootJsonFormat[DeviceRequest] = jsonFormat6(DeviceRequest)

  implicit val observationFormat: RootJsonFormat[Observation] = jsonFormat5(
    Observation)

  implicit val observationReqFormat: RootJsonFormat[ObservationRequest] = jsonFormat3(
    ObservationRequest)

}
