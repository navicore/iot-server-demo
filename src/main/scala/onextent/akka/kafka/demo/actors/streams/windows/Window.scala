package onextent.akka.kafka.demo.actors.streams.windows

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.streams.windows.Window.Window
import onextent.akka.kafka.demo.models.Observation

import scala.collection.mutable
import scala.concurrent.duration._


object Window {

  type Window = (Long, Long)

  val WindowLength: Long = 10.minutes.toMillis
  val WindowStep: Long =  1.minute .toMillis
  val WindowsPerEvent: Int = (WindowLength / WindowStep).toInt

  def windowsFor(ts: Long): Set[Window] = {
    val firstWindowStart = ts - ts % WindowStep - WindowLength + WindowStep
    (for (i <- 0 until WindowsPerEvent) yield
      (firstWindowStart + i * WindowStep,
        firstWindowStart + i * WindowStep + WindowLength)
      ).toSet
  }

}

sealed trait WindowCommand {
  def w: Window
}
case class AggregateEventData(w: Window, eventCount: Int)
case class OpenWindow(w: Window) extends WindowCommand
case class CloseWindow(w: Window) extends WindowCommand
case class AddToWindow(ev: (Observation, UUID), w: Window) extends WindowCommand

class CommandGenerator extends LazyLogging {
  private val MaxDelay = 30.seconds.toMillis
  private var watermark = 0L
  private val openWindows = mutable.Set[Window]()

  def forEvent(ev: (Observation, UUID)): List[WindowCommand] = {
    watermark = math.max(watermark, ev._1.datetime.toInstant.toEpochMilli - MaxDelay)
    if (ev._1.datetime.toInstant.toEpochMilli < watermark) {
      logger.warn(s"Dropping event with timestamp: ${ev._1.datetime}")
      Nil
    } else {
      val eventWindows = Window.windowsFor(ev._1.datetime.toInstant.toEpochMilli)

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
