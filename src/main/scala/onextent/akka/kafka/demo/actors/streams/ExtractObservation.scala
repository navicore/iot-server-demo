package onextent.akka.kafka.demo.actors.streams

import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf
import onextent.akka.kafka.demo.models.Observation
import onextent.akka.kafka.demo.models.functions.JsonSupport
import spray.json._

object ExtractObservation extends LazyLogging with Conf with JsonSupport {

  def apply(): CommittableMessage[Array[Byte], String] => (Observation,
                                                           CommittableMessage[
                                                             Array[Byte],
                                                             String]) = msg => {

    val observation: Observation =
      msg.record.value().parseJson.convertTo[Observation]

    (observation, msg)
  }

}
