package zio_layer

import java.time.DateTimeException

import zio.clock.Clock
import zio.console.Console
import zio.{Has, IO, UIO, ZLayer}

/** This logExample demonstrates how to compose a Logging ZLayer
  * horizontally from a Console ZLayer and a Clock ZLayer.
  * @author Christoph Knabe
  * @since 2020-04-10
  */
package object logExample {

  //Type alias for a layer, which has the service:
  type Logging = Has[Logging.Service]

  object Logging {

    //The service interface:
    trait Service {
      def logLine(line: String): IO[DateTimeException, Unit]
    }

    /** A service implementation, which simply logs to a console */
    val consoleLogging: ZLayer[Console, Nothing, Logging] =
      ZLayer.fromService { console =>
        new Logging.Service {
          override def logLine(line: String): UIO[Unit] =
            console.putStrLn(line)
        }
      }

    /**A service implementation, which logs with timestamp (thus needing a Clock) to a console. */
    val timestampedLogging: ZLayer[Console with Clock, Nothing, Logging]
    // The following generic parameters should be inferred, but currently the Scala compiler cannot do it.
    // See https://github.com/zio/zio/issues/2949
    = ZLayer.fromServices[Console.Service, Clock.Service, Logging.Service] {
      (console, clock) =>
        new Logging.Service {
          override def logLine(line: String): IO[DateTimeException, Unit] = {
            for {
              offsetDateTime <- clock.currentDateTime
              localDateTime = offsetDateTime.toLocalDateTime
              result <- console.putStrLn(s"$localDateTime $line")
            } yield result
          }
        }
    }

  }

}
