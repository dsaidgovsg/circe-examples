import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "circe-examples"
ThisBuild / organizationName := "circe-examples"

val circeVersion = "0.12.3"

lazy val root = (project in file("."))
  .settings(
    name := "circe-examples",
    scalacOptions ++= Seq(
      "-Ymacro-annotations",
    ),
    libraryDependencies ++=
      Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser",
      ).map(_ % circeVersion) ++
      Seq(
        "io.circe" %% "circe-generic-extras" % "0.12.2",
      ),
  )
