package onextent.iot.server.demo.actors.streams.functions

import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.Conf
import onextent.iot.server.demo.models.Observation
import onextent.iot.server.demo.models.functions.JsonSupport
import spray.json._

object ExtractObservations extends LazyLogging with Conf with JsonSupport {

  def apply(): CommittableMessage[Array[Byte], String] => (Observation,
                                                           CommittableMessage[
                                                             Array[Byte],
                                                             String]) = msg => {

    val observation: Observation =
      msg.record.value().parseJson.convertTo[Observation]

    (observation, msg)
  }

}
