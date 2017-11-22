package onextent.akka.kafka.demo.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, ExceptionHandler, RejectionHandler, Route}
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.concurrent.duration.{Duration, FiniteDuration}

trait HttpSupport extends LazyLogging {

  val conf: Config = ConfigFactory.load()
  val corsOriginList: List[HttpOrigin] = conf
    .getStringList("main.corsOrigin")
    .asScala
    .iterator
    .toList
    .map(origin => HttpOrigin(origin))
  val urlpath: String = conf.getString("main.path")
  val port: Int = conf.getInt("main.port")

  val corsSettings: CorsSettings.Default = CorsSettings.defaultSettings.copy(
    allowedOrigins = HttpOriginRange(corsOriginList: _*))

  val rejectionHandler
    : RejectionHandler = corsRejectionHandler withFallback RejectionHandler.default

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Exception =>
      logger.warn(s"do not know $e")
      complete(StatusCodes.BadRequest -> e.getMessage)
  }

  val handleErrors
    : Directive[Unit] = handleRejections(rejectionHandler) & handleExceptions(
    exceptionHandler)

  def HealthCheck: Route =
    path("healthcheck") {
      get {
        complete("ok")
      }
    }

  def requestTimeout: Timeout = {
    val t = conf.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }

  implicit val timeout: Timeout = requestTimeout
}
