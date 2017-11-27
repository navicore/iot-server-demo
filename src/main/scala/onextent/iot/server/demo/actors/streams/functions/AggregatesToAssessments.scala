package onextent.iot.server.demo.actors.streams.functions

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.models.Assessment

object AggregatesToAssessments extends LazyLogging {

  def apply()(agg: AggregateEventData): List[(Assessment, UUID)] = {

    val from: ZonedDateTime = ZonedDateTime.from(
      Instant.ofEpochMilli(agg.w._2).atOffset(ZoneOffset.UTC))

    val hour = "%02d".format(from.getHour)

    val minute = "%02d".format(from.getMinute / 10 * 10)

    def round(d: Double) = Math.round(d * 100.0) / 100.0

    agg.w match {

      case (_, _, rootName, locationId) =>

        List(
          (Assessment(s"${rootName}_${hour}_${minute}_count",
                      agg.values.length,
                      from),
           locationId),
          (Assessment(s"${rootName}_${hour}_${minute}_ave",
                      round(agg.values.sum / agg.values.length),
                      from),
           locationId),
          (Assessment(s"${rootName}_${hour}_${minute}_min",
                      agg.values.min,
                      from),
           locationId),
          (Assessment(s"${rootName}_${hour}_${minute}_max",
                      agg.values.max,
                      from),
           locationId)
        )
    }

  }

}
