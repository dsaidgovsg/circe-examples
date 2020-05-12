package circeeg.util

import enumeratum._
import io.circe.generic.extras.ConfiguredJsonCodec

import circeeg.util.Conf.custom

// DO NOT USE @ConfiguredJsonCodec for any of the enums because it will override
// the custom enum naming behavior

@ConfiguredJsonCodec
final case class Demo(
  ages: Option[Set[AgeBand]],
  genders: Option[Set[Gender]]
)

//
// Age band
//

sealed abstract class AgeBand(override val entryName: String) extends EnumEntry

case object AgeBand extends Enum[AgeBand] with CirceEnum[AgeBand] {
  case object UnderTwenty extends AgeBand("<20")
  case object TwentyToTwentyFour extends AgeBand("20-24")
  case object TwentyFiveToTwentyNine extends AgeBand("25-29")
  case object ThirtyToThirtyFour extends AgeBand("30-34")
  case object ThirtyFiveToThirtyNine extends AgeBand("35-39")
  case object FortyToFortyFour extends AgeBand("40-44")
  case object FortyFiveToFortyNine extends AgeBand("45-49")
  case object FiftyToFiftyFour extends AgeBand("50-54")
  case object FiftyFiveToFiftyNine extends AgeBand("55-59")
  case object SixtyToSixtyFour extends AgeBand("60-64")
  case object SixtyFiveToSixtyNine extends AgeBand("65-69")
  case object AboveSeventy extends AgeBand(">70")
  case object NotAvailable extends AgeBand("unknown")

  val values = findValues
}

//
// Gender
//

sealed abstract class Gender(override val entryName: String) extends EnumEntry

case object Gender extends Enum[Gender] with CirceEnum[Gender] {
  case object Male extends Gender("male")
  case object Female extends Gender("female")
  case object NotAvailable extends Gender("unknown")

  val values = findValues
}
