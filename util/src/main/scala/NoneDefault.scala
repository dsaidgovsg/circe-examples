package circeeg.util

trait Default[+T] {
  def default: T
}

object NoneDefault {
  implicit class NoneDefaultExt[T](opt: Option[T]) {
    final def noneToDefault(implicit d: Default[T]): T =
      opt match {
        case Some(v) => v
        case None => d.default
      }
  }

  implicit object defaultUnit extends Default[Unit] {
    def default: Unit = ()
  }

  implicit object defaultBoolean extends Default[Boolean] {
    def default: Boolean = false
  }

  implicit object defaultByte extends Default[Byte] {
    def default: Byte = 0
  }

  implicit object defaultShort extends Default[Short] {
    def default: Short = 0
  }

  implicit object defaultInt extends Default[Int] {
    def default: Int = 0
  }

  implicit object defaultLong extends Default[Long] {
    def default: Long = 0L
  }

  implicit object defaultFloat extends Default[Float] {
    def default: Float = 0.0F
  }

  implicit object defaultDouble extends Default[Double] {
    def default: Double = 0.0
  }

  implicit object defaultChar extends Default[Char] {
    def default: Char = '\u0000'
  }

  implicit object defaultString extends Default[String] {
    def default: String = ""
  }

  implicit def defaultSeq[A]: Default[Seq[A]] = new Default[Seq[A]] {
    def default: Seq[A] = Seq()
  }

  implicit def defaultSet[A]: Default[Set[A]] = new Default[Set[A]] {
    def default: Set[A] = Set()
  }

  implicit def defaultMap[A, B]: Default[Map[A, B]] = new Default[Map[A, B]] {
    def default: Map[A, B] = Map()
  }

  implicit def defaultOption[A]: Default[Option[A]] = new Default[Option[A]] {
    def default: Option[A] = None
  }
}
