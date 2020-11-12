package zio_trial

import zio.clock.Clock
import zio.console.Console
import zio.{ExitCode, ZIO, ZLayer}
import zio.logging._

/** Derived from `Simple` on https://zio.github.io/zio-logging/docs/overview/overview_index.html */
object HelloConsoleLogZioApp extends zio.App {

  val env: ZLayer[Console with Clock, Nothing, Logging] =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("my-component")

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, ExitCode] =
    log.info("Hello from ZIO logger").provideCustomLayer(env).as(ExitCode.success)

}
