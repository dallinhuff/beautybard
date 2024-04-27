val tapirVersion = "1.10.5"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "beautybard",
    version := "0.1.0-SNAPSHOT",
    organization := "co.beautybard",
    scalaVersion := "3.4.1",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "dev.zio" %% "zio-logging" % "2.2.2",
      "dev.zio" %% "zio-logging-slf4j" % "2.2.3",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "dev.zio" %% "zio-test" % "2.0.22" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.22" % Test,
      "com.softwaremill.sttp.client3" %% "zio-json" % "3.9.5" % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
)
