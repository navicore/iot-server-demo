package onextent.iot.server.demo.actors.streams.functions.observations

import akka.kafka.ConsumerMessage.CommittableMessage
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}

/**
  * mapConcat helper to drop records that have no location
  */
object FilterDevicesWithLocations {

  def apply[K,V](): ((EnrichedAssessment[Device], CommittableMessage[K, V])) => List[
    (EnrichedAssessment[Device], CommittableMessage[K, V])] = t => {
    t._1.enrichment.location match {
      case Some(_) =>
        List(t)
      case _ =>
        t._2.committableOffset.commitScaladsl()
        List()
    }
  }

}
