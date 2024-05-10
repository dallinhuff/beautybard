package co.beautybard.component

import co.beautybard.page.NotFoundPage
import co.beautybard.page.home.HomePage
import co.beautybard.page.login.{LoginPage, RegisterPage}
import co.beautybard.page.products.ProductsPage
import co.beautybard.page.profile.ProfilePage
import co.beautybard.page.reviews.ReviewsPage
import com.raquo.laminar.api.L.{*, given}
import frontroute.*

object Router {
  def apply(): Element =
    mainTag(
      routes(
        div(
          cls := "relative px-8",
          (pathEnd | path("home"))(HomePage()),
          path("products")(ProductsPage()),
          path("reviews")(ReviewsPage()),
          path("login")(LoginPage()),
          path("register")(RegisterPage()),
          path("profile")(ProfilePage()),
          noneMatched(NotFoundPage())
        )
      )
    )
}
