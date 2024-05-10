package co.beautybard.component

import com.raquo.laminar.api.L.*

object ThemeControl {
  def apply(css: String = "w-6 h-6"): HtmlElement =
    label(
      cls := "swap swap-rotate",
      input(
        tpe := "checkbox",
        cls := "theme-controller",
        value := "dark",
      ),
      svg.svg(
        svg.cls := s"swap-off $css",
        svg.xmlns := "http://www.w3.org/2000/svg",
        svg.fill := "none",
        svg.viewBox := "",
        svg.viewBox := "0 0 24 24",
        svg.strokeWidth := "1.5",
        svg.stroke := "currentColor",
        svg.path(
          svg.strokeLineCap := "round",
          svg.strokeLineJoin := "round",
          svg.d := "M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773-4.227-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z",
        ),
      ),
      svg.svg(
        svg.cls := s"swap-on $css",
        svg.xmlns := "http://www.w3.org/2000/svg",
        svg.fill := "none",
        svg.viewBox := "",
        svg.viewBox := "0 0 24 24",
        svg.strokeWidth := "1.5",
        svg.stroke := "currentColor",
        svg.path(
          svg.strokeLineCap := "round",
          svg.strokeLineJoin := "round",
          svg.d := "M21.752 15.002A9.72 9.72 0 0 1 18 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 0 0 3 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 0 0 9.002-5.998Z"
        )
      )
    )
}
