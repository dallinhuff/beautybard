package co.beautybard.component

import com.raquo.laminar.api.L.*

sealed trait LoadingSpinner(size: String):
  def apply(): HtmlElement =
    span(cls := s"loading loading-spinner $size")
  
object LoadingSpinner:
  case object Small  extends LoadingSpinner("loading-sm")
  case object Medium extends LoadingSpinner("loading-md")
  case object Large  extends LoadingSpinner("loading-lg")
