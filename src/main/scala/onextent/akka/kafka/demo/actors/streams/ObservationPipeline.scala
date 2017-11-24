package onextent.akka.kafka.demo.actors.streams

import akka.Done
import akka.actor.ActorRef
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf.{consumerSettings, parallelism, topic}
import onextent.akka.kafka.demo.Conf._

import scala.concurrent.Future

object ObservationPipeline extends LazyLogging {

  def apply(deviceService: ActorRef, locationService: ActorRef)(implicit timeout: Timeout): Future[Done] = {
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(parallelism) {
        ObservationConsumer(deviceService)
      }
      .mapAsync(parallelism) {
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          msg.committableOffset.commitScaladsl()
      }
      .runWith(Sink.ignore)
  }
}
