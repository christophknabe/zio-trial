package zio_trial

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.test.junit.JUnitRunnableSpec

object EffectfulSpecWithJUnit {

  def sayHello: ZIO[Console, Nothing, Unit] =
    console.putStrLn("Hello, World!")

}

class EffectfulSpecWithJUnit extends JUnitRunnableSpec {

  import EffectfulSpecWithJUnit._

  def spec =
    suite(getClass.getSimpleName)(
      testM("sayHello correctly displays output") {
        for {
          _ <- sayHello
          output <- TestConsole.output
        } yield assert(output)(equalTo(Vector("Hello, World!\n")))
        //returns a ZIO
      }
    )

}
