package onextent.akka.kafka.demo.actors.streams

import akka.Done
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, Subscriptions}
import akka.stream.scaladsl.Sink
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf.{consumerSettings, parallelism, topic}
import onextent.akka.kafka.demo.Conf._

import scala.concurrent.Future

object Consume extends LazyLogging {

  def apply(): Future[Done] = {
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic))
      .mapAsync(parallelism) {
        ObservationConsumer()
      }
      .mapAsync(parallelism) {
        (msg: ConsumerMessage.CommittableMessage[Array[Byte], String]) =>
          msg.committableOffset.commitScaladsl()
      }
      .runWith(Sink.ignore)
  }
}
