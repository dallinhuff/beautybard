val tapirVersion = "1.10.6"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "beautybard",
    version := "0.1.0-SNAPSHOT",
    organization := "co.beautybard",
    scalaVersion := "3.4.1",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "org.http4s" %% "http4s-ember-server" % "0.23.26",
      "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.6",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "com.softwaremill.sttp.client3" %% "circe" % "3.9.5" % Test
    )
  )
)
