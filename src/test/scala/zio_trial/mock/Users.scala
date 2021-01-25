package zio_trial.mock

import zio.{Has, UIO, URIO, ZIO, ZLayer}
import zio_trial.mock.Files.Files

/** ZIO module representing a set of users. */
object Users {

  type Users = Has[Users.Service]

  trait Service {

    /** Returns the total amount of bytes used by the named user. */
    def spaceUsedByUser(username: String): UIO[Long]
  }

  /** A ZLayer for Users based on Files which adds the file sizes of the subfiles "A" and "B" of the user.*/
  val summingFilesAB: ZLayer[Files, Nothing, Users] =
    ZLayer.fromService { files =>
      new Users.Service {
        override def spaceUsedByUser(username: String): UIO[Long] =
          for {
            aSize <- files.fileSize(s"$username/A")
            bSize <- files.fileSize(s"$username/B")
          } yield aSize + bSize
      }
    }

  //Access functions:
  def spaceUsedByUser(username: String): ZIO[Files, Nothing, Long] = ZIO.accessM(_.get.spaceUsedByUser(username))

}

/** ZIO module representing a set of files. */
object Files {

  type Files = Has[Files.Service]

  trait Service {

    /** Returns the amount of bytes used by the file at the given `path`. */
    def fileSize(path: String): UIO[Long]
  }

}
