import Dependencies._

// scalafix has bug when RemoveUnused and SortImports are used together
// Need to specifically do RemoveUnused in order to fix things
// https://gitter.im/scalacenter/scalafix?at=5e8aa6655d148a0460e8fe64
// We use aliases to mask the issue
addCommandAlias("fix", "compile:scalafix; test:scalafix; compile:scalafix RemoveUnused; test:scalafix RemoveUnused")
addCommandAlias("check", "compile:scalafix --check; test:scalafix --check")

val circeVersion = "0.12.0-M3"
val enumeratumCirceVersion = "1.5.23"
val silencerVersion = "1.6.0"

inThisBuild(
  Seq(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "com.nequissimus" %% "sort-imports" % "0.3.2",

    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
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
    // Cannot use "-Ywarn-unused" because of stupid bugs like
    // https://github.com/scala/bug/issues/11918
    "-Ywarn-unused:-patvars,_",
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
