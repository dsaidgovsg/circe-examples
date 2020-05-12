import Dependencies._

val circeVersion = "0.12.0-M3"
val enumeratumCirceVersion = "1.5.23"
val silencerVersion = "1.6.0"

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
    ) ++
    Seq(
      // Scalafix warning silencer (only required for < Scala 2.13)
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
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
