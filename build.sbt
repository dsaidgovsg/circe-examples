import Dependencies._

val circeVersion = "0.12.0-M3"
val enumeratumCirceVersion = "1.5.23"

inThisBuild(
  Seq(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.3.2"
  )
)

// The set-up no longer supports for 2.11
val commonSettings = Seq(
  scalaVersion := "2.12.8",
  version := "0.1.0-SNAPSHOT",
  organization := "circeeg",
  organizationName := "circeeg",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Ywarn-unused",
    "-Yrangepos"
  ),
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

lazy val util = (project in file("util"))
  .aggregate(extras)
  .dependsOn(extras)
  .settings(
    commonSettings
  )

lazy val root = (project in file("."))
  .aggregate(util)
  .dependsOn(util)
  .settings(
    commonSettings
  )
