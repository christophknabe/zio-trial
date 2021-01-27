package zio_trial.mock

import zio.{Has, UIO, ZIO, ZLayer}
import zio_trial.mock.Files.Files

/** ZIO module representing a set of users.
  * This module depends on an implementation of the {@link Files} module.
  * @since 2021-01-27
  * @author Christoph Knabe
  */
object Users {

  type Users = Has[Users.Service]

  trait Service {

    /** Returns the total amount of bytes used by the named user. */
    def spaceUsedByUser(username: String): UIO[Long]
  }

  /** A ZLayer for Users based on Files which adds the file sizes of the subfiles "A" and "B" of the user.*/
  val recipe: ZLayer[Files, Nothing, Users] =
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
  def spaceUsedByUser(username: String): ZIO[Users, Nothing, Long] = ZIO.accessM(_.get.spaceUsedByUser(username))

}
