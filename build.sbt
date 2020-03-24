import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "circeeg"
ThisBuild / organizationName := "circeeg"

val circeVersion = "0.12.3"
val enumeratumCirceVersion = "1.5.23"

lazy val root = (project in file("."))
  .settings(
    name := "circe-examples",
    libraryDependencies ++=
      Seq(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser",
      ).map(_ % circeVersion) ++
      Seq(
        "io.circe" %% "circe-generic-extras" % "0.12.2",
      ) ++
      Seq(
        "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion
      ),
  )

// Macro paradise
resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
