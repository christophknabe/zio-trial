package zio_trial

import java.lang.invoke.MethodHandles
import java.time.Duration
import java.util.concurrent.{CompletableFuture, CompletionStage, ThreadLocalRandom}

import zio._
import zio.console._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

object ZioExampleSpec extends DefaultRunnableSpec {

  def print(msg: String): UIO[Unit] = ZIO.effectTotal(println(msg))
  def multiply(x: Int, y: Int): UIO[Int] = ZIO.effectTotal(x * y)
  def toLong(string: String): Task[Long] = Task.effect(string.toLong)

  def spec =
    suite(MethodHandles.lookup.lookupClass.getSimpleName.stripSuffix("$"))(
      testM("multiply correctly multiplies in for comprehension") {
        val testResult = for {
          product <- multiply(2, 3)
          _ <- print(s"2*3=$product")
        } yield assert(product)(equalTo(6))
        testResult
      },
      testM("multiply correctly multiplies without for comprehension") {
        val testResult = multiply(2, 3).map(product => assert(product)(equalTo(6)))
        testResult
      },
      testM("ZIO.fromCompletionStage captures a Java CompletableFuture") {
        val cf: CompletableFuture[Int] =
          CompletableFuture.supplyAsync(() => ThreadLocalRandom.current().nextInt(1, 10))
        val zioResult: Task[Int] = ZIO.fromCompletionStage(cf)
        val testResult = zioResult.map(product => assert(product)(approximatelyEquals(5, 4)))
        testResult
      },
      testM("multiply, convert ZIO to CompletableFuture, and back in for comprehension") {
        val productZio = multiply(2, 3)
        val completableFutureZio = productZio.toCompletableFuture
        val testResult = for {
          cf: CompletionStage[Int] <- completableFutureZio
          zioResult = ZIO.fromCompletionStage(cf)
          product <- zioResult
          _ <- print(s"2*3=$product")
        } yield assert(product)(equalTo(6))
        testResult
      },
      testM("toLong success") {
        val zioResult: Task[Long] = toLong("123")
        val testResult = zioResult.map(n => assert(n)(equalTo(123L)))
        testResult
      },
      testM("toLong with unparsable argument") {
        //when
        val zioResult: Task[Long] = toLong("12A3")
        val testResult = zioResult.flip.map { th =>
          assert(th)(isSubtype[NumberFormatException](anything)) &&
          assert(th.getMessage)(equalTo("""For input string: "12A3""""))
        }
        testResult
      },
      testM("reduce a Seq of ZIOs") {
        val numberStrings = (1 to 10).map(_.toString)
        val numberTasks: Seq[Task[Long]] = numberStrings.map(toLong)
        val sumTask: Task[Long] = ZIO.reduceAll(Task(0L), numberTasks)(_ + _)
        assertM(sumTask)(equalTo(55L))
      }
    )

}
