import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / name         := "beautybard"
ThisBuild / organization := "co.beautybard"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.4.1"
ThisBuild / scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

val tapirVersion = "1.10.6"

val commonDependencies = Seq(
  "com.softwaremill.sttp.tapir"   %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.client3" %% "circe"            % "3.9.5"
)

lazy val common = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/common"))
  .settings(libraryDependencies ++= commonDependencies)
  .jsSettings(libraryDependencies ++= Seq())

val serverDependencies = commonDependencies ++ Seq(
  "com.github.pureconfig"       %% "pureconfig-cats-effect"   % "0.17.6",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % tapirVersion,
  "org.http4s"                  %% "http4s-ember-server"      % "0.23.26",
  "com.softwaremill.sttp.tapir" %% "tapir-prometheus-metrics" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"  % tapirVersion,
  "ch.qos.logback"               % "logback-classic"          % "1.5.6",
  "org.tpolecat"                %% "skunk-core"               % "0.6.3",
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server"   % tapirVersion % Test,
  "org.scalatest"               %% "scalatest"                % "3.2.18"     % Test
)

lazy val server = project
  .in(file("modules/server"))
  .settings(libraryDependencies ++= serverDependencies)
  .dependsOn(common.jvm)

lazy val app = project
  .in(file("modules/app"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("co.beautybard")))
    },
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0",
      "com.raquo"    %%% "laminar"     % "15.0.1"
    )
  )
  .dependsOn(common.js)

lazy val root = project
  .in(file("."))
  .aggregate(server)
