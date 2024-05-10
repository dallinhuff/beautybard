package co.beautybard.page.home

import co.beautybard.component.StarRating
import com.raquo.laminar.api.L.*

object HomePage {
  def apply(): HtmlElement =
    val rtg = Var(0)
    div(
      "feed",
      StarRating("str", rtg.signal, rtg.writer)
    )
}
