package onextent.akka.kafka.demo.actors.streams.windows

import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.actors.streams.windows.Window.Window
import onextent.akka.kafka.demo.models.Observation

import scala.collection.mutable
import scala.concurrent.duration._


object Window {

  type Window = (Long, Long)

  val WindowLength: Long = 10.seconds.toMillis
  val WindowStep: Long =  1.second .toMillis
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
case class AddToWindow(ev: Observation, w: Window) extends WindowCommand

class CommandGenerator extends LazyLogging {
  private val MaxDelay = 5.seconds.toMillis
  private var watermark = 0L
  private val openWindows = mutable.Set[Window]()

  def forEvent(ev: Observation): List[WindowCommand] = {
    watermark = math.max(watermark, ev.datetime.toInstant.toEpochMilli - MaxDelay)
    if (ev.datetime.toInstant.toEpochMilli < watermark) {
      logger.warn(s"Dropping event with timestamp: ${ev.datetime}")
      Nil
    } else {
      val eventWindows = Window.windowsFor(ev.datetime.toInstant.toEpochMilli)

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
