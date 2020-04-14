package zio_layer.logExample

import java.time.DateTimeException

import zio.clock.Clock
import zio.console.Console
import zio.{ZIO}

/** Logs a message with a timestamp to the console. */
object Main extends zio.App {

  /**Runs the effect model as an app. If errors occur, they have to be mapped to an Int != 0 inside.*/
  def run(args: List[String]): ZIO[Console with Clock, Nothing, Int] = {
    //By providing the timestampedLogging for the needed Logging, we still need a Console and a Clock,
    //which is reflected in in the result type's R parameter.
    myAppLogic.provideSomeLayer(Logging.timestampedLogging).fold(failure => 1, success => 0)
  }

  /**The app logic as an effect model.
    * The logic needs a Logging layer, can fail with a DateTimeException,
    * or succeeds returning nothing (all it does is a side effect). */
  val myAppLogic: ZIO[Logging, DateTimeException, Unit] = ZIO.accessM{
    logging =>
    logging.get.logLine("Hello! Here I am.")
  }
}
