package zio_trial

import java.io.IOException

import zio.{Cause, Runtime, ZEnv, ZIO}
import zio.console.{Console, getStrLn, putStrLn}

/** Adapted from [[https://zio.dev/docs/getting_started.html]]
  * Error Reporting:
  * 1. Reports exceptions thrown during execution without their stack trace, as this is useless in async code.
  * 2. Reports the ZIO execution trace, and the planned next execution step, if an exception occured.
  * 3. Exits with an error code (0 if successful, 1 if erroneous).
  *
  * @author Christoph Knabe
  * @since 2020-03-03 */
object HelloErrorReportingApp extends scala.App {

  val runtime = Runtime.default.withReportFailure(_reportFailure) //ZIO 1.0.0-RC18-2

  /** Runs the effect model as an app. All unhandled exceptions cause termination and are reported by [[_reportFailure]]. */
  val exitCode = runtime.unsafeRunSync(myAppLogic)
    // All errors are mapped to exitCode 1, all successes to exitCode 0.
    .fold(c => 1, a => {println("ZIO app completed successfully."); 0})
  System.exit(exitCode)

  /** The app logic as an effect model.
    * If you provoke an exception by typing in `<Ctrl/D>`, it will be
    * propagated by the ZIO error channel, here of type `IOException`. */
  def myAppLogic: ZIO[ZEnv, IOException, Unit] = for {
    _ <- putStrLn("Hello! What is your name?")
    name <- getStrLn
    _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
  } yield ()

  /** Synchronously reports the failure `cause` with a detailed async execution trace to scala.Console.err.
    * In contrary to the default failure reporter it reports only the causing exception, but not the useless stack trace of it.  */
  private def _reportFailure(cause: Cause[Any]): Unit = {
    val failures = cause.failures.mkString("ZIO App failed with:\n", "\n", "\n")
    val traces = cause.traces.map(_.prettyPrint).mkString("\n", "\n", "\n")
    val report = failures + traces
    scala.Console.err.println(report)
  }

}