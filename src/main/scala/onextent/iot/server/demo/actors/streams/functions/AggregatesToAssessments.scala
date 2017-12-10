package onextent.iot.server.demo.actors.streams.functions

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.models.Assessment

private object round {
  def apply(d: Double): Double = Math.round(d * 100.0) / 100.0
}

object AggregatesToAssessments extends LazyLogging {

  def apply()(agg: AggregateEventData): List[(Assessment, UUID)] = {

    agg.w match {

      case (_, stopTime, rootName, locationId) =>
        val from: ZonedDateTime = ZonedDateTime.from(
          Instant.ofEpochMilli(stopTime).atOffset(ZoneOffset.UTC))

        val hour = "%02d".format(from.getHour)

        val minute = "%02d".format(from.getMinute / 10 * 10)

        List(
          (Assessment(s"${rootName}_${hour}_${minute}_count",
                      agg.values.length,
                      from),
           locationId),
          (Assessment(s"${rootName}_${hour}_${minute}_sum",
                      round(agg.values.sum),
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

object AggrergateAggregatesToAssessments extends LazyLogging {

  def round(d: Double): Double = Math.round(d * 100.0) / 100.0

  def apply()(agg: AggregateEventData): List[(Assessment, UUID)] = {

    agg.w match {

      case (_, stopTime, rootName, locationId) =>
        val from: ZonedDateTime = ZonedDateTime.from(
          Instant.ofEpochMilli(stopTime).atOffset(ZoneOffset.UTC))

        rootName match {
          case n if n.contains("_count") =>
            List((Assessment(s"$rootName", agg.values.sum, from), locationId))
          case n if n.contains("_sum") =>
            List((Assessment(s"$rootName", agg.values.sum, from), locationId))
          case n if n.contains("_ave") =>
            List(
              (Assessment(s"$rootName",
                          round(agg.values.sum / agg.values.length),
                          from),
               locationId))
          case n if n.contains("_min") =>
            List((Assessment(s"$rootName", agg.values.min, from), locationId))
          case n if n.contains("_max") =>
            List((Assessment(s"$rootName", agg.values.max, from), locationId))
          case _ => List()
        }
    }
  }
}
