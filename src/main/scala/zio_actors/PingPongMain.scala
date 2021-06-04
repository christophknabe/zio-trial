package zio_actors

import zio.ExitCode.{failure, success}
import zio.actors.Actor.Stateful
import zio.actors._
import zio.{RIO, Runtime, UIO, ZIO}
import zio.console._

//Taken from https://github.com/zio/zio-actors/blob/master/docs/usecases/pingpong.md
//but without Remoting
object PingPongMain {

  sealed trait PingPong[+_]
  case class Ping(sender: ActorRef[PingPong]) extends PingPong[Unit]
  case object Pong extends PingPong[Unit]
  case class GameInit(recipient: ActorRef[PingPong]) extends PingPong[Unit]

  val protoHandler = new Stateful[Console, Unit, PingPong] {

    override def receive[A](
        state: Unit,
        msg: PingPong[A],
        context: Context
    ): RIO[Console, (Unit, A)] = {
      msg match {
        case Ping(sender) =>
          for {
            _ <- putStrLn("Ping!")
            _ <- sender ! Pong
          } yield ((), ())

        case Pong =>
          for {
            _ <- putStrLn("Pong!")
          } yield ((), ())

        case GameInit(to) =>
          for {
            self <- context.self[PingPong]
            _ <- to ! Ping(self)
          } yield ((), ())
      }
    }

  }

  val program = for {
    actorSystem <- ActorSystem("PingPongActorSystem")
    firstPlayer <- actorSystem.make("firstPlayer", Supervisor.none, (), protoHandler)
    secondPlayer <- actorSystem.make("secondPlayer", Supervisor.none, (), protoHandler)

    _ <- firstPlayer ! GameInit(secondPlayer)
  } yield ()

  def main(args: Array[String]): Unit = {
    val minSeconds = 2
    import zio.duration._
    val delayed = UIO.unit.delay(minSeconds.seconds).map(_ => s"$minSeconds seconds passed")
    val exitCode = Runtime.default.unsafeRunSync(program.zipPar(delayed))
      .fold(_ => failure, result => { println(s"ZIO App completed successfully with result $result"); success })
    System.exit(exitCode.code)
  }

}
