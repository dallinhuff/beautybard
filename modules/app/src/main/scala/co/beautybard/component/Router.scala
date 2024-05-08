package co.beautybard.component

import co.beautybard.page.NotFoundPage
import co.beautybard.page.feed.FeedPage
import co.beautybard.page.login.LoginPage
import co.beautybard.page.profile.ProfilePage
import co.beautybard.page.register.RegisterPage
import com.raquo.laminar.api.L.{*, given}
import frontroute.*

object Router {
  def apply(): Element =
    mainTag(
      routes(
        div(
          cls := "px-8",
          (pathEnd | path("feed"))(FeedPage()),
          path("login")(LoginPage()),
          path("register")(RegisterPage()),
          path("profile")(ProfilePage()),
          noneMatched(NotFoundPage())
        )
      )
    )
}
