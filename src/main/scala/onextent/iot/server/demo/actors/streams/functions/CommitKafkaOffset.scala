package onextent.iot.server.demo.actors.streams.functions

import akka.kafka.ConsumerMessage
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object CommitKafkaOffset extends LazyLogging {

  def apply[A, B, K, V]()(implicit timeout: Timeout, ec: ExecutionContext)
    : ((A, ConsumerMessage.CommittableMessage[K, V])) => Future[
      (A, ConsumerMessage.CommittableMessage[K, V])] = {

    (t: (A, ConsumerMessage.CommittableMessage[K, V])) =>
      {

        val promise =
          Promise[(A, ConsumerMessage.CommittableMessage[K, V])]()

        t._2.committableOffset.commitScaladsl().onComplete {
          case Success(_) => promise.success(t)
          case Failure(e) =>
            logger.error(s"commit to kafka: $e")
            promise.failure(e)
        }
        promise.future
      }
  }

}
