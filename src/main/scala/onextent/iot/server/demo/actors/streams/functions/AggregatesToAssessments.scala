package onextent.iot.server.demo.actors.streams.functions

import java.time.{Instant, ZoneOffset, ZonedDateTime}
import java.util.UUID

import onextent.iot.server.demo.models.Assessment

object AggregatesToAssessments {

  def apply()(agg: AggregateEventData): List[(Assessment, UUID)] = {

    val from: ZonedDateTime = ZonedDateTime.from(
      Instant.ofEpochMilli(agg.w._2).atOffset(ZoneOffset.UTC))

    val hour = "%02d".format(from.getHour)

    val minute = "%02d".format(from.getMinute / 10 * 10)

    def round(d: Double) = Math.round(d * 100.0) / 100.0

    List(
      (Assessment(s"${agg.w._3}_${hour}_${minute}_count",
                  agg.values.length,
                  from),
       agg.w._4),
      (Assessment(s"${agg.w._3}_${hour}_${minute}_ave",
                  round(agg.values.sum / agg.values.length),
                  from),
       agg.w._4),
      (Assessment(s"${agg.w._3}_${hour}_${minute}_min", agg.values.min, from),
       agg.w._4),
      (Assessment(s"${agg.w._3}_${hour}_${minute}_max", agg.values.max, from),
       agg.w._4)
    )

  }

}
