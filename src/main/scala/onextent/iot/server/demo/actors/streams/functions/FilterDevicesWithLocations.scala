package onextent.iot.server.demo.actors.streams.functions

import akka.kafka.ConsumerMessage.CommittableMessage
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}

/**
  * mapConcat helper to drop records that have no location
  */
object FilterDevicesWithLocations {

  def apply[K, V]()
    : ((EnrichedAssessment[Device], CommittableMessage[K, V])) => List[
      (EnrichedAssessment[Device], CommittableMessage[K, V])] = {

    case t @ (eh, msg) =>
      eh.enrichment.location match {
        case Some(_) =>
          List(t)
        case _ =>
          msg.committableOffset.commitScaladsl()
          List()
      }
  }

}
