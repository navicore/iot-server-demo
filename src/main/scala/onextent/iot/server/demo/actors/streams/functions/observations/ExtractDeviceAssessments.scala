package onextent.iot.server.demo.actors.streams.functions.observations

import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.models.functions.JsonSupport
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}
import spray.json._

object ExtractDeviceAssessments extends LazyLogging with Conf with JsonSupport {

  def apply()
    : CommittableMessage[Array[Byte], String] => (EnrichedAssessment[Device],
                                                  CommittableMessage[
                                                    Array[Byte],
                                                    String]) = msg => {

    val assessment: EnrichedAssessment[Device] =
      msg.record.value().parseJson.convertTo[EnrichedAssessment[Device]]

    (assessment, msg)
  }

}
