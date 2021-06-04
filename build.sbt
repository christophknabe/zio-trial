//Christoph Knabe, 2020-02-27

// Leave an empty line between each key definition by := !

name := "zio-trial"

version := "1.1"

scalaVersion := "2.13.2"

// https://mvnrepository.com/artifact/dev.zio/zio lists all versions
val zioVersion = "1.0.3"
val circeVersion = "0.13.0" //on Scala 2.12
val sttpVersion = "2.0.7"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % sttpVersion, //for STTP
  "com.softwaremill.sttp.client" %% "circe" % sttpVersion, //for Circe interop with STTP client
  "io.circe" %% "circe-core" % circeVersion, //without JS dependency
  "io.circe" %% "circe-generic" % circeVersion, //without JS dependency
  "io.circe" %% "circe-parser" % circeVersion, //without JS dependency
  "org.slf4j" % "slf4j-simple" % "1.7.30", //avoid Failed to load class "org.slf4j.impl.StaticLoggerBinder"
  "dev.zio" %% "zio-logging-slf4j" % "0.5.3", //version number independent of ZIO
  // https://mvnrepository.com/artifact/dev.zio/zio-actors
  "dev.zio" %% "zio-actors" % "0.0.9",
//test dependencies:
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test, // optional
  "dev.zio" %% "zio-test-junit" % zioVersion % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test

  //See https://github.com/rickynils/scalacheck/blob/master/examples/simple-sbt/build.sbt
  /*The operator %% builds an artifact name from the specified scalaVersionDependentArtifact name,
   * an underscore sign, and the upper mentioned scalaVersion.
   * So the artifact name will result here in scalatest_2.12,
   * as the last number in a Scala version is not API relevant.
   */
)

//See http://www.scalatest.org/user_guide/using_scalatest_with_sbt
logBuffered in Test := false

//Used for ScalaCheck, see https://github.com/rickynils/scalacheck/blob/master/examples/simple-sbt/build.sbt
testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-maxSize", "5", "-minSuccessfulTests", "33", "-workers", "1", "-verbosity", "1")

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

//Tell the SBT Eclipse plugin to download all sources along with binary .jar files and make them available for source code navigation. Only if the SBT Eclipse plugin is activated:
//EclipseKeys.withSource := true
