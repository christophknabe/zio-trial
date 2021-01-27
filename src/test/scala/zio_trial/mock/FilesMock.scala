package zio_trial.mock

import zio.{Has, IO, UIO, URLayer, ZLayer}
import zio.test.mock.{Mock, Proxy}
import zio_trial.mock.Files.Files

object FilesMock extends Mock[Files] {

  object FileSize extends Effect[String, Nothing, Long]

  val compose: URLayer[Has[Proxy], Files] = {
    ZLayer.fromService(proxy =>
      new Files.Service {
        override def fileSize(path: String): UIO[Long] = proxy(FileSize, path)
      }
    )
  }

}
