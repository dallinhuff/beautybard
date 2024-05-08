package co.beautybard

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import frontroute.*
import co.beautybard.component.*

object App:
  private val app =
    div(
      onMountCallback(_ => () /* TODO */),
      Header(),
      Router(),
    ).amend(LinkHandler.bind)

  def main(args: Array[String]): Unit =
    val containerNode = dom.document.querySelector("#app")
    render(containerNode, app)