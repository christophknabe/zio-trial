package zio_trial

/**
  * @author Christoph Knabe
  */

import java.io.IOException

import zio.{ExitCode, ZIO}
import zio.console.{Console, getStrLn, putStrLn}

/** Adapted from https://zio.dev/docs/getting_started.html
  * with the for-comprehension transformed to a series of flatMap functions, and one map function. */
object HelloMapZioApp extends zio.App {

  /** Runs the effect model as an app. */
  def run(args: List[String]): ZIO[Console, Nothing, ExitCode] = {
    import ExitCode._
    myAppLogic.fold(_ => failure, _ => success)
  }

  /** The app logic as an effect model */
  val myAppLogic: ZIO[Console, IOException, Unit] = {
    putStrLn("Hello! What is your name?").flatMap { _ =>
      getStrLn.flatMap {
        name =>
          putStrLn(s"Hello, ${name}, welcome to ZIO!").map {
            _ => ()
          }
      }
    }
  }
}