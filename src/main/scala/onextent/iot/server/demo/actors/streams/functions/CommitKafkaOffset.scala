package onextent.iot.server.demo.actors.streams.functions

import akka.kafka.ConsumerMessage
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

object CommitKafkaOffset extends LazyLogging {

  def apply[A, K, V]()(implicit timeout: Timeout, ec: ExecutionContext)
    : ((A, ConsumerMessage.CommittableMessage[K, V])) => Future[
      (A, ConsumerMessage.CommittableMessage[K, V])] =
    (t: (A, ConsumerMessage.CommittableMessage[K, V])) =>
      t._2.committableOffset.commitScaladsl().map(_ => t)

}
