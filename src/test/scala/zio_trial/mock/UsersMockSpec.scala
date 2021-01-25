package zio_trial.mock

import zio.console
import zio.test.Assertion.{equalTo, isUnit}
import zio.test.mock.Expectation.value
import zio.test.mock.MockConsole
import zio.test.{DefaultRunnableSpec, assertM, suite, testM}

object UsersMockSpec extends DefaultRunnableSpec {

  def spec =
    suite(getClass.getSimpleName)(
      testM("expect call with input satisfying assertion") {
        val app = console.putStrLn("foo")
        val env = MockConsole.PutStrLn(equalTo("foo"))
        val out = app.provideLayer(env)
        assertM(out)(isUnit)
      },
      testM("expect call with method returning value") {
        val app = console.getStrLn
        val env = MockConsole.GetStrLn(value("bar"))
        val out = app.provideLayer(env)
        assertM(out)(equalTo("bar"))
      }
    )

}
