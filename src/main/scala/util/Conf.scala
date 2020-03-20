package circeeg.util

import io.circe.generic.extras._

package object conf {
  // This makes all members to be snake_case
  implicit val config: Configuration = Configuration.default
    .withSnakeCaseConstructorNames
    .withSnakeCaseMemberNames
}
