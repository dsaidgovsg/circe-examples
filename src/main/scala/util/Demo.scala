package circeeg.util

import enumeratum._

import circeeg.util.conf._

// DO NOT USE @ConfiguredJsonCodec here because it will override the
// custom enum naming behavior

sealed abstract class AgeDemo(override val entryName: String) extends EnumEntry

case object AgeDemo extends Enum[AgeDemo] with CirceEnum[AgeDemo] {
  case object UnderTwenty extends AgeDemo("<20")
  case object TwentyToTwentyFour extends AgeDemo("20-24")
  case object TwentyFiveToTwentyNine extends AgeDemo("25-29")
  case object ThirtyToThirtyFour extends AgeDemo("30-34")
  case object ThirtyFiveToThirtyNine extends AgeDemo("35-39")
  case object FortyToFortyFour extends AgeDemo("40-44")
  case object FortyFiveToFortyNine extends AgeDemo("45-49")
  case object FiftyToFiftyFour extends AgeDemo("50-54")
  case object FiftyFiveToFiftyNine extends AgeDemo("55-59")
  case object SixtyToSixtyFour extends AgeDemo("60-64")
  case object SixtyFiveToSixtyNine extends AgeDemo("65-69")
  case object AboveSeventy extends AgeDemo(">70")
  case object AgeNotAvailable extends AgeDemo("unknown")

  val values = findValues
}
