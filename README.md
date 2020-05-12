# Circe Examples

Experimental Scala set-up to try auto-decoding for `circe`.

This also uses `enumeratum` with `circe`-integration.

## How to build and run

You should only require the `sbt` command.

Simply run `sbt run` at the root directory to run the main example program.

## What should I take note

The most important thing of note is the succient declarative code set-up for the
various types of values.

For the standard kind of `circe` serialization and deserialization (let's call
it serde) involving time and duration, look at:
[`Filter.scala`](util/src/main/scala/Filter.scala).

For enumeration related set-up, look at:
[`Demo.scala`](util/src/main/scala/Demo.scala).
