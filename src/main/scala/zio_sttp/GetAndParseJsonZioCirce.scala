package zio_sttp

/** Derived from the STTP example
  * https://github.com/softwaremill/sttp/blob/master/examples/src/main/scala/sttp/client/examples/GetAndParseJsonZioCirce.scala */

import sttp.client._
import sttp.client.circe._
import sttp.client.asynchttpclient.zio._
import io.circe.generic.auto._
import zio._
import zio.console.Console

//See https://sttp.softwaremill.com/en/latest/examples.html
object GetAndParseJsonZioCirce extends App {

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {

    case class HttpBinResponse(origin: String, headers: Map[String, String])

    val request = basicRequest
      .get(uri"https://httpbin.org/get")
      .response(asJson[HttpBinResponse])

    // create a description of a program, which requires two dependencies in the environment:
    // the SttpClient, and the Console
    val sendAndPrint: ZIO[Console with SttpClient, Throwable, Unit] = for {
      response <- SttpClient.send(request)
      _ <- console.putStrLn(s"Got response code: ${response.code}")
      _ <- console.putStrLn(response.body.toString)
    } yield ()

    // provide an implementation for the SttpClient dependency; other dependencies are
    // provided by Zio
    sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer()).fold(_ => 1, _ => 0)
  }
}