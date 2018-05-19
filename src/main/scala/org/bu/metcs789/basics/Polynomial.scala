package org.bu.metcs789.basics


/**
  * Finite polynomial
  * @param coeffs - input coefficients
  */
class Polynomial(coeffs: Double*) extends (Double => Double){

  val coefficients: Seq[Double] = if(coeffs.isEmpty) Seq(0) else if(coeffs.forall(_ == 0)) Seq(0) else coeffs.reverse.dropWhile(_ == 0).reverse
  val degree: Int = Math.max(0, coefficients.size - 1)

  /**
    * Factors a polynomial into a set of irreducible polynomials whose product equals this polynomial
    * If this polynomial is irreducible, this function returns a singleton set containing this polynomial
    * @return Seq[Polynomial]
    */
  lazy val factors: Set[Polynomial] = Set()
  lazy val isReducible: Boolean = factors.size > 1
  lazy val derivative = Polynomial(coefficients.indices.map{ i =>coefficients(i) * i}.drop(1):_*)
  lazy val antiDerivative = Polynomial(Array.fill(1)(0.0).toSeq ++ coefficients.indices.map{ i => coefficients(i) * 1.0/(i+1)}:_*)

  override def apply(v1: Double): Double = coefficients.indices.map{ i => coefficients(i) * Math.pow(v1, i)}.sum
  override def toString(): String =
    if(this == Polynomial.zero) "0.0"
    else {
      coefficients.indices.map { i =>
        val coeffStr = coefficients(i) match {
          case c if c == 0 || (c == 1 && i != 0) => ""
          case c if c < 0 => s"($c)"
          case c => s"$c"
        }
        val expStr = i match {
          case exp if exp == 0 || coefficients(i) == 0 => ""
          case exp if exp == 1 => "x"
          case exp => s"x^$exp"
        }
        s"$coeffStr$expStr"
      }.filter(_.nonEmpty).reverse.mkString(" + ")
    }

  def ^ (exp: Int): Polynomial = if(exp == 0) Polynomial(1) else this * (this ^ (exp-1))
  def * (other: Polynomial): Polynomial = {
    coefficients.indices.map{ i =>
      val newCoeffs = Array.fill(i)(0.0).toSeq ++ other.coefficients.map( c => c * coefficients(i))
      Polynomial(newCoeffs:_*)
    }.reduce((p1, p2) => p1 + p2)
  }

  def == (other: Polynomial): Boolean = this.coefficients == other.coefficients

  def - (other: Polynomial) = Polynomial(this.coefficients.zipAll(other.coefficients, 0.0, 0.0).map{case(a,b) => a-b}:_*)
  def + (other: Polynomial) = Polynomial(this.coefficients.zipAll(other.coefficients, 0.0, 0.0).map{case(a,b) => a+b}:_*)
  def / (other: Polynomial): (Polynomial, Polynomial) = {
    var quotient = Polynomial.zero
    var remainder = this
    while(remainder.degree >= other.degree && remainder != Polynomial.zero) {
      val tempVal = Polynomial(0, 1) ^ (remainder.degree - other.degree)
      if(remainder.coefficients.reverse.head < 0) {
        remainder += tempVal * other
        quotient -= tempVal
      }
      else {
        remainder -= tempVal * other
        quotient += tempVal
      }
    }
    (quotient, remainder)
  }
  def % (other: Polynomial): Polynomial = (this/other)._2
  def integral(lowerBound: Double, upperBound: Double): Double = antiDerivative(upperBound) - antiDerivative(lowerBound)
}

object Polynomial{
  def apply(coefficients: Double*): Polynomial = new Polynomial(coefficients:_*)
  def zero = new Polynomial(0)
  def one = new Polynomial(1)
}
