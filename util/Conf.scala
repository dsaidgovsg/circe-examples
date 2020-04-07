package circeeg.util

import io.circe.generic.extras.Configuration

object Conf {
  // This makes all members to be snake_case
  implicit val custom: Configuration = Configuration.default
    .withSnakeCaseConstructorNames
    .withSnakeCaseMemberNames
}
