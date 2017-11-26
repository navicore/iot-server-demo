package onextent.akka.kafka.demo

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings}
import akka.pattern.AskTimeoutException
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{
  ByteArrayDeserializer,
  ByteArraySerializer,
  StringDeserializer,
  StringSerializer
}

import scala.concurrent.ExecutionContextExecutor

object Conf extends Conf {

  implicit val actorSystem: ActorSystem = ActorSystem("akka_kafka_demo")
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val decider: Supervision.Decider = {
    case _: AskTimeoutException => Supervision.Resume
    case _                      => Supervision.Stop
  }

  implicit val materializer: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem).withSupervisionStrategy(decider))

  val consumerSettings: ConsumerSettings[Array[Byte], String] =
    ConsumerSettings(actorSystem,
                     new ByteArrayDeserializer,
                     new StringDeserializer)
      .withBootstrapServers(bootstrap)
      .withGroupId(consumerGroup)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val producerSettings: ProducerSettings[Array[Byte], String] =
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(bootstrap)

}

trait Conf {

  val conf: Config = ConfigFactory.load()

  val appName: String = conf.getString("main.appName")
  val bootstrap: String = conf.getString("kafka.bootstrap")
  val consumerGroup: String = conf.getString("kafka.consumerGroup")
  val topic: String = conf.getString("kafka.topic")
  val parallelism: Int = conf.getInt("kafka.parallelism")

}
