package zio_trial

import zio.clock.Clock
import zio.console.Console
import zio.logging._
import zio.logging.slf4j._
import zio.{ExitCode, ULayer, ZIO, ZLayer}

/** Derived from `Slf4jAndCorrelationId` on https://zio.github.io/zio-logging/docs/overview/overview_index.html */
object HelloSlf4jLogZioApp extends zio.App {

  val logFormat = "[correlation-id = %s] %s"

  val env: ULayer[Logging] =
    Slf4jLogger.make { (context, message) => message }

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    log.info("Hello from ZIO logger").provideCustomLayer(env).as(ExitCode.success)

}
