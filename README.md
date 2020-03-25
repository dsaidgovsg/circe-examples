# Circe Examples

Experimental Scala set-up to try auto-decoding for `circe`.

This also uses `enumeratum` with `circe`-integration.

## How to build and run

You should only require the `sbt` command.

Go to `main` directory, and simply run `sbt run` and you should see the
example output.

## What should I take note

The most important thing of note is the succient declarative code set-up for the
various types of values.

For the standard kind of `circe` serialization and deserialization (let's call
it serde) involving time and duration, look at:
[`Filter.scala`](src/main/scala/util/Filter.scala).

For enumeration related set-up, look at:
[`Demo.scala`](src/main/scala/util/Demo.scala).
