package zio_layer.sttpExample

import sttp.client.{NothingT, Request, RequestT, Response, SttpBackend, SttpClientException}
import zio.{Has, Task, ZIO, ZLayer}
import zio.console.Console

object sttpclient {

  //Type alias for a layer, which has the service:
  type SttpClient = Has[SttpClient.Service]

  object SttpClient {

    //The service interface:
    trait Service {

      /**
        * Sends the request. Only requests for which the method & URI are specified can be sent.
        *
        * @return An effect resulting in a [[Response]], containing the body, deserialized as specified by the request
        *         (see [[RequestT.response]]), if the request was successful (1xx, 2xx, 3xx response codes), or if there
        *         was a protocol-level failure (4xx, 5xx response codes).
        *
        *         A failed effect, if an exception occurred when connecting to the target host, writing the request or
        *         reading the response.
        *
        *         Known exceptions are converted to one of [[SttpClientException]]. Other exceptions are kept unchanged.
        */
      def send[T](request: Request[T, Nothing]): ZIO[SttpClient, Throwable, Response[T]]

    }

    /** A service implementation, which uses only the traditional style of web acess,
      * that means sends a request and expects a response. */
    //val traditional: ZLayer[Console, Nothing, SttpClient] =

  }

}
