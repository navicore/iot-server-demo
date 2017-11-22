package onextent.akka.kafka.demo.actors.streams

import akka.kafka.ConsumerMessage._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.kafka.demo.Conf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ObservationConsumer extends LazyLogging with Conf {

  def apply(): CommittableMessage[Array[Byte], String] => Future[
    CommittableMessage[Array[Byte], String]] = {

    (msg: CommittableMessage[Array[Byte], String]) =>
      val value = msg.record.value()

      val key = new String(
        Option(msg.record.key())
          .getOrElse(value.hashCode().toString.getBytes("UTF8")))

      logger.debug(s"key: $key value: $value")

      // todo: look up a device and send the observation
      Future { msg }
  }

}
