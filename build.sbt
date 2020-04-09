import Dependencies._

val circeVersion = "0.12.0-M3"
val enumeratumCirceVersion = "1.5.23"

val commonSettings = Seq(
  scalaVersion := "2.11.8",
  version := "0.1.0-SNAPSHOT",
  organization := "circeeg",
  organizationName := "circeeg",
  libraryDependencies ++=
    Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-generic-extras",
      "io.circe" %% "circe-parser",
    ).map(_ % circeVersion) ++
    Seq(
      "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion,
    )
)

lazy val extras = (project in file("extras"))
  .settings(
    commonSettings
  )

lazy val util = (project in file("util")).dependsOn(extras)
  .settings(
    commonSettings
  )

lazy val main = (project in file("main")).dependsOn(util)
  .settings(
    commonSettings
  )

lazy val root = (project in file("."))
  .aggregate(main)
