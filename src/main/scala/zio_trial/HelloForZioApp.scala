package zio_trial

/**
 * @author ${user.name}
 */
import java.io.IOException

import zio.{ExitCode, ZIO}
import zio.console.{Console, getStrLn, putStrLn}

/**Adapted from https://zio.dev/docs/getting_started.html */
object HelloForZioApp extends zio.App {

  /**Runs the effect model as an app. All errors have to be mapped to an Int != 0 inside.*/
  def run(args: List[String]): ZIO[Console, Nothing, ExitCode] = {
    import ExitCode._
    myAppLogic.fold(_ => failure, _ => success)
  }

  /**The app logic as an effect model.
    * The logic needs a Console, and either fails with an IOException,
    * or succeeds returning nothing (all it does is a side effect). */
  val myAppLogic: ZIO[Console, IOException, Unit] =
    for {
      _ <- putStrLn("Hello! What is your name?") //flatMap
      name <- getStrLn //flatMap
      _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!") //map
    } yield ()
}