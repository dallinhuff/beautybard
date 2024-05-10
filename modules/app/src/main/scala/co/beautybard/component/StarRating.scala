package co.beautybard.component

import com.raquo.laminar.api.L.*

object StarRating {
  def apply(rtgName: String, value: Signal[Int], updater: Observer[Int]): HtmlElement =
    div(
      cls := "rating rating-half",
      input(
        tpe      := "radio",
        nameAttr := rtgName,
        cls      := "rating-hidden",
        onClick.mapTo(0) --> updater
      ),
      (1 to 10).map: i =>
        input(
          tpe      := "radio",
          nameAttr := rtgName,
          cls      := "bg-secondary mask mask-star-2",
          cls      := (if i % 2 == 0 then "mask-half-2" else "mask-half-1"),
          selected <-- value.map(_ == i),
          onClick.mapTo(i) --> updater
        )
    )

  def apply(rtgName: String, bindTo: Var[Int]): HtmlElement =
    apply(rtgName, bindTo.signal, bindTo.writer)
}
