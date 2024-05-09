package co.beautybard.component

import com.raquo.laminar.api.L.{*, given}
import co.beautybard.core.Session

object Header:
  def apply(): Element =
    div(
      cls := "navbar bg-base-100",
      div(
        cls := "navbar-start",
        a(href := "/", cls := "btn btn-ghost text-xl", "BeautyBard"),
      ),
      div(
        cls := "navbar-center",
        ul(
          cls := "menu menu-horizontal px-1",
          li(a(href := "/feed", "Feed")),
          li(a(href := "/products", "Products")),
          li(a(href := "/reviews", "Reviews"))
        )
      ),
      div(
        cls := "navbar-end gap-2",
        ul(
          cls := "menu menu-horizontal px-1",
          children <-- Session.userState.signal.map: t =>
            if t.isEmpty then
              li(a(href := "/login", "Log in")) :: Nil
            else
              li(a(href := "/profile", "Profile")) :: Nil
        )
      )
    )
