package zio_trial

import zio.test.junit.JUnitRunnableSpec
import zio.test.{ assert, suite, test, Assertion }

/** Taken from https://github.com/zio/zio/blob/master/examples/jvm/src/test/scala/zio/examples/test/ExampleSpecWithJUnit.scala */
class PureSpecWithJUnit extends JUnitRunnableSpec {
  def spec = suite(getClass.getSimpleName)(
    test("failing pure test") {
      assert(1)(Assertion.equalTo(2))
    },
    test("passing pure test") {
      assert(1)(Assertion.equalTo(1))
    }
  )
}