package zio_trial

import java.io.{EOFException, IOException}

import zio.{Cause, IO, Runtime, ZEnv, ZIO}
import zio.console.putStrLn

/** Program for manually testing the ZIO Error Reporting. Adapted from [[https://zio.dev/docs/getting_started.html]]
  *
  * '''Happy path''': Type in at the prompts your first name, and your last name. You will be helloed with your full name.
  *
  * '''Expected exception''': Type at the first prompt '''Ctrl/D''' in order to signal an `EOFException`. This is expected in the
  * ZIO error channel, and could thus be handled. As it is not handled, the ZIO Runtime will report it.
  *
  * '''Unexpected exception''': Type at the second prompt '''Ctrl/D''' in order to signal an `EOFException`. This is unexpected in the
  * ZIO error channel, and cannot be handled. Only the ZIO Runtime will report it.
  *
  * The app exits with an error code (0 if successful, 1 if erroneous).
  *
  * @author Christoph Knabe
  * @since 2020-03-03 */
object HelloErrorReportingScalaApp extends scala.App {

  val runtime = Runtime.default.withReportFailure(_reportCause) //ZIO 1.0.0-RC18-2

  /** Runs the effect model as an app. All unhandled ZIO errors, as well as all unhandled Throwables
    * cause termination and are reported to stderr by the ZIO runtime.
    * They can be reported alternatively by `.withReportFailure(_reportCause)`. */
  val exitCode = runtime.unsafeRunSync(myAppLogic)
  // All errors are mapped to exitCode 1, all successes to exitCode 0.
    .fold(cause => 1, result => { println("ZIO App completed successfully."); 0 })

  System.exit(exitCode)

  /** The app logic as an effect model.
    * If you provoke an exception by typing in `<Ctrl/D>`, it will be
    * propagated by the ZIO expected error channel, here of type `IOException`. */
  def myAppLogic: ZIO[ZEnv, EOFException, Unit] = {
    for {
      _ <- putStrLn("Hello! What is your first name?")
      firstName <- failableGetStrLn()
      _ <- putStrLn("And what is your last name?")
      lastName <- unfailableGetStrLn()
      _ <- putStrLn(s"Hello, $firstName $lastName, welcome to ZIO!")
    } yield ()
  }

  /** Synchronously reports the death or failure `cause` with a detailed async execution trace to scala.Console.err.
    * In contrary to the default failure reporter it reports only the causing exception, but not the useless stack trace of it.  */
  private def _reportCause(cause: Cause[Any]): Unit = {
    val prefix = "ZIO App "
    val defects = if (cause.defects.isEmpty) "" else cause.defects.map(_makeStackTrace).mkString("died with:\n", "\n", "\n")
    val failures = if (cause.failures.isEmpty) "" else cause.failures.mkString("failed with:\n", "\n", "\n")
    val traces = cause.traces.map(_.prettyPrint).mkString("\n", "\n", "\n")
    val report = prefix + defects + failures + traces
    scala.Console.err.println(report)
  }

  // The following code is adapted from zio.console
  import scala.io.StdIn
  import scala.{Console => SConsole}

  /**
    * Retrieves a line of input from the console.
    * Unexpected exceptions are propagated by the hidden ZIO Throwable channel.
    */
  //final lazy val failableGetStrLn: ZIO[Any, IOException, String] = failableGetStrLn(SConsole.in)

  /**
    * Retrieves a line of input from the `reader`.
    * Fails expectedly when the underlying [[java.io.Reader]]
    * returns null.
    */
  final def failableGetStrLn(): ZIO[Any, EOFException, String] =
    IO.effect(SConsole.withIn(SConsole.in) {
      val line = StdIn.readLine()
      if (line == null) {
        throw new EOFException("Expected EOF: There is no more input left to read")
      } else line
    }).refineToOrDie[EOFException]

  /**
    * Retrieves a line of input from the console.
    * Unexpected exceptions as EOFException, and others, are propagated by the hidden ZIO Throwable channel.
    */
  //final lazy val unfailableGetStrLn: ZIO[Any, Nothing, String] = unfailableGetStrLn(SConsole.in)

  /**
    * Retrieves a line of input from the `reader`.
    * Fails unexpectedly when the underlying [[java.io.Reader]]
    * returns null.
    */
  final def unfailableGetStrLn(): ZIO[Any, Nothing, String] =
    IO.effectTotal(SConsole.withIn(SConsole.in) {
      val line = StdIn.readLine()
      if (line == null) {
        throw new EOFException("Unexpected EOF: There is no more input left to read")
      } else line
    })

  private def _makeStackTrace(throwable: Throwable): String = {
    val result = new StringBuilder(throwable.toString)
    for (elem <- throwable.getStackTrace) {
      result append "\n\tat "
      result append elem
    }
    result.toString()
  }

}
