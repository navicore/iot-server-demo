package onextent.iot.server.demo.actors.streams.functions

import java.util.UUID

import akka.kafka.ConsumerMessage.CommittableMessage
import com.typesafe.scalalogging.LazyLogging
import onextent.iot.server.demo.actors.streams.functions.Window.Window
import onextent.iot.server.demo.models.{Device, EnrichedAssessment}

import scala.collection.mutable
import scala.concurrent.duration._

object Window {

  type Window = (Long, Long, String, UUID)

  val WindowLength: Long = 10.minutes.toMillis
  val WindowStep: Long = 1.minute.toMillis
  val WindowsPerEvent: Int = (WindowLength / WindowStep).toInt

  def windowsFor(ts: Long, name: String, forId: UUID): Set[Window] = {
    val firstWindowStart = ts - ts % WindowStep - WindowLength + WindowStep
    (for (i <- 0 until WindowsPerEvent)
      yield
        (firstWindowStart + i * WindowStep,
         firstWindowStart + i * WindowStep + WindowLength,
         name,
         forId)).toSet
  }

}

sealed trait WindowCommand {
  def w: Window
}
case class AggregateEventData(w: Window, values: List[Double] = List[Double]())
case class OpenWindow(w: Window) extends WindowCommand
case class CloseWindow(w: Window) extends WindowCommand
case class AddToWindow[K, V](
    ev: (EnrichedAssessment[Device], CommittableMessage[K, V]),
    w: Window)
    extends WindowCommand

class CommandGenerator extends LazyLogging {
  private val MaxDelay = 10.seconds.toMillis
  private var watermark = 0L
  private val openWindows = mutable.Set[Window]()

  def forEvent[K, V](ev: (EnrichedAssessment[Device], CommittableMessage[K, V]))
    : List[WindowCommand] = {
    watermark = math.max(
      watermark,
      ev._1.assessment.datetime.toInstant.toEpochMilli - MaxDelay)
    if (ev._1.assessment.datetime.toInstant.toEpochMilli < watermark) {
      logger.warn(
        s"Dropping event with timestamp: ${ev._1.assessment.datetime}")
      Nil
    } else {
      val eventWindows = Window.windowsFor(
        ev._1.assessment.datetime.toInstant.toEpochMilli,
        ev._1.assessment.name,
        ev._1.enrichment.location.getOrElse(UUID.randomUUID()))

      val closeCommands = openWindows.flatMap { ow =>
        if (!eventWindows.contains(ow) && ow._2 < watermark) {
          openWindows.remove(ow)
          Some(CloseWindow(ow))
        } else None
      }

      val openCommands = eventWindows.flatMap { w =>
        if (!openWindows.contains(w)) {
          openWindows.add(w)
          Some(OpenWindow(w))
        } else None
      }

      val addCommands = eventWindows.map(w => AddToWindow(ev, w))

      openCommands.toList ++ closeCommands.toList ++ addCommands.toList
    }
  }

}
