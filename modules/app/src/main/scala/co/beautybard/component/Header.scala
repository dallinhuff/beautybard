package co.beautybard.component

import com.raquo.laminar.api.L.{*, given}
import co.beautybard.core.Session
import co.beautybard.icon.{Home, ShoppingBag, Star, UserCircle}

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
          li(a(href := "/home", Home("h-5"), "Home")),
          li(a(href := "/products", ShoppingBag("h-5"), "Products")),
          li(a(href := "/reviews", Star("h-5"), "Reviews"))
        )
      ),
      div(
        cls := "navbar-end gap-2",
        ul(
          cls := "menu menu-horizontal px-1",
          children <-- Session.userState.signal.map: t =>
            if t.isEmpty then
                li(a(href := "/login", UserCircle("h-5"), "Log in"))
                :: li(ThemeControl("h-5"))
                :: Nil
            else
              li(ThemeControl())
                :: li(a(href := "/profile", "Profile"))
                :: Nil
        )
      )
    )
