package zio_trial

import java.io.EOFException

import zio.console.putStrLn
import zio.{Cause, IO, Runtime, UIO, ZEnv, ZIO}

/** Program for testing how to run a ZIO app by [[Runtime.unsafeRunAsync]]. Adapted from [[https://zio.dev/docs/getting_started.html]]
  *
  * '''Happy path''': Type in at the prompts your first name, and your last name. You will be helloed with your full name.
  *
  * '''Expected exception''': Type at the first prompt '''Ctrl/D''' in order to signal an [[EOFException]]. This is expected in the
  * ZIO failure channel, and could thus be handled. As it is not handled, `withReportFailure` will report it.
  *
  * '''Unexpected exception''': Type at the second prompt '''Ctrl/D''' in order to signal an `EOFException`. This is unexpected in the
  * ZIO failure channel, and cannot be handled. Only the ZIO Runtime or `withReportFailure` will report it.
  *
  * The app exits with an error code (0 if successful, 1 if erroneous).
  *
  * @author Christoph Knabe
  * @since 2020-11-27 */
object HelloUnsafeRunAsyncScalaApp extends scala.App {

  val runtime = Runtime.default

  /** Runs the effect model as an app. All unhandled ZIO failures, as well as all unhandled defects
    * cause termination and are reported to stderr by the ZIO runtime. */
  runtime.unsafeRunAsync(myAppLogic)(exit =>
    exit // All errors are mapped to exitCode 1, all successes to exitCode 0.
      .fold(cause => System.exit(1), result => { println("ZIO App completed successfully."); System.exit(0) })
  )
  Thread.sleep(10000)

  /** The app logic as an effect model.
    * If you provoke an exception by typing in `<Ctrl/D>`, it will be
    * propagated by the ZIO failure channel for modelled errors, here of type [[EOFException]]. */
  def myAppLogic: ZIO[ZEnv, EOFException, Unit] = {
    for {
      _ <- putStrLn("Hello! What is your first name?")
      firstName <- failableGetStrLn()
      _ <- putStrLn("And what is your last name?")
      lastName <- unfailableGetStrLn()
      _ <- putStrLn(s"Hello, $firstName $lastName, welcome to ZIO!")
    } yield ()
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
  final def failableGetStrLn(): IO[EOFException, String] =
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
  final def unfailableGetStrLn(): UIO[String] =
    UIO(SConsole.withIn(SConsole.in) {
      val line = StdIn.readLine()
      if (line == null) {
        throw new EOFException("Unexpected EOF: There is no more input left to read")
      } else line
    })

}
