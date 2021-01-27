package zio_trial.mock

import zio.{Has, UIO}

/** ZIO module representing a set of files.
  * @since 2021-01-27
  * @author Christoph Knabe */
object Files {

  type Files = Has[Files.Service]

  trait Service {

    /** Returns the amount of bytes used by the file at the given `path`. */
    def fileSize(path: String): UIO[Long]
  }

}
