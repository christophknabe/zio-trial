package zio_actors

import zio.ExitCode.{failure, success}
import zio.Runtime

/** Following https://zio.github.io/zio-actors/docs/overview/overview_basics */
object DoublerMain {

  // Domain Model:
  sealed trait Command[+_]
  case class DoubleCommand(value: Int) extends Command[Int]

  import zio.actors.Actor.Stateful
  import zio.actors._
  import zio.UIO

  // The doubler actor behavior:
  val stateful = new Stateful[Any, Unit, Command] {

    override def receive[A](state: Unit, msg: Command[A], context: Context): UIO[(Unit, A)] = {
      msg match {
        case DoubleCommand(value) => UIO(((), value * 2))
      }
    }

  }

  def main(args: Array[String]): Unit = {
    val myAppLogic = for {
      system <- ActorSystem("mySystem")
      actor <- system.make("actor1", Supervisor.none, init = (), stateful)
      doubled <- actor ! DoubleCommand(42)
      _ <- system.shutdown
    } yield doubled

    /** Runs the effect model as an app. All unhandled ZIO errors, as well as all unhandled Throwables
      * cause termination and are reported to stderr by the ZIO runtime.
      * They can be reported alternatively by `.withReportFailure(_reportCause)`. */
    val exitCode = Runtime.default.unsafeRunSync(myAppLogic)
    // All errors are mapped to exitCode 1, all successes to exitCode 0.
      .fold(cause => failure, result => { println(s"ZIO App completed successfully with result $result"); success })

    System.exit(exitCode.code)
  }

}
