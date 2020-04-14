package zio_layer

/** Old style modularization following the module pattern as in the example of
  * https://docs.google.com/document/d/1P0mx1gSNU2UTi-9PXUeY8O74v5gyK-UETznM1SbkQjA/ */
package object old_style {
  import zio._
  import zio.console.Console

  trait Logging {
    def logging: Logging.Service
  }

  object Logging {

    trait Service {
      def logLine(line: String): UIO[Unit]
    }

    def make(console: Console): Logging = new Logging {
        val logging = new Logging.Service {
          override def logLine(line: String): UIO[Unit] =
            //console.console.putStrLn(line) //was so in ZIO 1 RC17, but is not compilable with RC 18
            console.get.putStrLn(line) //instead with RC 18
        }
      }
  }

  trait Authorization {
    val authorization: Authorization.Service
  }

  object Authorization {

    trait Service {
      def key: Long
    }

    object Live extends Authorization {
      val authorization = new Authorization.Service {
        val key = 123L
      }
    }
  }

  trait Database[K, V] {
    def database: Database.Service[K, V]
  }

  object Database {

    trait Service[K, V] {
      def get(key: K): UIO[Option[V]]
      def set(key: K, value: V): UIO[Unit]
    }

    final case class InMemory[K, V](
                                     state: Ref[Map[K, V]],
                                     logging: Logging.Service,
                                     authorization: Authorization.Service
                                   ) extends Database.Service[K, V] {
      override def get(key: K): UIO[Option[V]] =
        state.get.map(_.get(key))
      override def set(key: K, value: V): UIO[Unit] =
        state.update(_.updated(key, value)).unit
    }

    def make[K, V](
                    logging: Logging,
                    authorization: Authorization
                  ): UIO[Database[K, V]] =
      Ref.make(Map.empty[K, V]).map { state =>
        new Database[K, V] {
          val database =
            InMemory(state, logging.logging, authorization.authorization)
        }
      }
  }


}
