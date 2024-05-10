package co.beautybard.icon

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.svg.*

trait HeroIcon(pathD: String) {
  def apply(css: String = "w-6 h-6"): L.SvgElement =
    svg(
      xmlns := "http://www.w3.org/2000/svg",
      fill := "none",
      viewBox := "0 0 24 24",
      strokeWidth := "1.5",
      stroke := "currentColor",
      cls := css,
      path(
        strokeLineCap := "round",
        strokeLineJoin := "round",
        d := pathD
      )
    )
}
