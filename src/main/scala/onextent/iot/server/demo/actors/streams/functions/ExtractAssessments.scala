package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.models.Assessment
import onextent.iot.server.demo.models.functions.JsonSupport
import spray.json._

object ExtractAssessments extends LazyLogging with Conf with JsonSupport {

  def apply(): CommittableMessage[Array[Byte], String] => ((Assessment, UUID),
                                                           CommittableMessage[
                                                             Array[Byte],
                                                             String]) = msg => {

    val assessment: Assessment =
      msg.record.value().parseJson.convertTo[Assessment]
    val keyStr = new String(msg.record.key())
    val destId: UUID = UUID.fromString(keyStr)
    ((assessment, destId), msg)
  }

}
