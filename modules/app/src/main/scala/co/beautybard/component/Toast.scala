package co.beautybard.component

import com.raquo.laminar.api.L.*

case class ToastMessage(level: String, message: String)

object Toast {
  def apply(content: Signal[List[ToastMessage]], css: String = "toast-end"): HtmlElement =
    div(
      cls := s"toast $css",
      children <-- content.map: messages =>
        messages.map:
          case ToastMessage(level, message) => div(cls := s"alert $level", span(message))
    )
}
