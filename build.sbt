import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

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

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
