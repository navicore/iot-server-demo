package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.models.Assessment
import onextent.iot.server.demo.models.functions.JsonSupport
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json._

object AssessmentProducerMessage
    extends LazyLogging
    with Conf
    with JsonSupport {

  def apply[K, V](
      ev: (Assessment, UUID)): ProducerRecord[Array[Byte], String] = {
    new ProducerRecord[Array[Byte], String](
      locationAssessmentsTopic,
      ev match {
        case (_, destId) =>
          destId.toString.getBytes("UTF8")
      },
      ev match {
        case (assessment, _) => assessment.toJson.prettyPrint
      }
    )
  }
}
