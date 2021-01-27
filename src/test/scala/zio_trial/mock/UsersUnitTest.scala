package zio_trial.mock

import zio.test.Assertion.{equalTo}
import zio.test.mock.Expectation.value
import zio.test.{DefaultRunnableSpec, assertM, suite, testM}

/** An example for a true unit test, which tests the {@link Users} module providing a mock implementation for the {@link Files} module.
  * @since 2021-01-27
  * @author Christoph Knabe
  */
object UsersUnitTest extends DefaultRunnableSpec {

  def spec =
    suite(getClass.getSimpleName)(
      testM("expect 2 calls to Files.fileSize") {
        val app = Users.spaceUsedByUser("knabe")
        val mockEnv = FilesMock.FileSize(equalTo("knabe/A"), value(17)) ++ FilesMock.FileSize(equalTo("knabe/B"), value(23))
        val env = mockEnv >>> Users.recipe
        val out = app.provideLayer(env)
        assertM(out)(equalTo(40L))
      }
    )

}
