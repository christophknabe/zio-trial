package zio_trial

/**
  * @author ${user.name}
  */
import java.io.IOException

import zio.console.{Console, getStrLn, putStrLn}
import zio.{ExitCode, ZIO}

/**Adapted from https://zio.dev/docs/getting_started.html */
object ExceptionMaskingZioApp extends zio.App {

  /**Runs the effect model as an app. All errors have to be mapped to an Int != 0 inside.*/
  def run(args: List[String]): ZIO[Console, Nothing, ExitCode] = {
    import ExitCode._
    myAppLogic.fold(_ => failure, _ => success)
  }

  /**The app logic as an effect model.
    * The logic needs a Console, and either fails with an IOException,
    * or succeeds returning nothing (all it does is a side effect). */
  val myAppLogic: ZIO[Console, IOException, Unit] =
    for {
      _ <- putStrLn("Hello! What is your name?") //flatMap
      name <- getStrLn //flatMap
      checkedName = name match {
        case x if x.isEmpty =>
          throw new IllegalArgumentException(s"You must indicate a name with at least one character!")
        case x => x
      }
      _ <- putStrLn(s"Hello, ${checkedName}, welcome to ZIO!") //map
    } yield ()

  /*
  def checkInternalResource(internalIRI: IRI): Task[Unit] =
    for {
      internalResource <- zioRS.get(internalIRI)
      result = internalResource match {
        case resource if resource != MISSING_RESOURCE && resource != DELETED_RESOURCE =>
          throw new IllegalArgumentException(s"Rejecting POD creation, because the internal resource $internalIRI already exists.")
        case _ => ()
      }
    } yield result
   */

}
