package onextent.akka.kafka.demo.actors.streams

import akka.kafka.ConsumerMessage
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

object CommitKafkaOffset extends LazyLogging {

  def apply[A, B]()(implicit timeout: Timeout,
                    ec: ExecutionContext): ((A,
                                             B,
                                             ConsumerMessage.CommittableMessage[
                                               Array[Byte],
                                               String])) => Future[
    (A, B, ConsumerMessage.CommittableMessage[Array[Byte], String])] = {

    (t: (A, B, ConsumerMessage.CommittableMessage[Array[Byte], String])) =>
      {

        val promise =
          Promise[(A,
                   B,
                   ConsumerMessage.CommittableMessage[Array[Byte], String])]()

        t._3.committableOffset.commitScaladsl().onComplete {
          case Success(_) => promise.success(t)
          case Failure(e) =>
            logger.error(s"commit to kafka: $e")
            promise.failure(e)
        }
        promise.future
      }
  }

}
