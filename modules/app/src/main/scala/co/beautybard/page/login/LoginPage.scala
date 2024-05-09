package co.beautybard.page.login

import co.beautybard.core.Session
import co.beautybard.domain.data.user.UserToken
import com.raquo.laminar.api.L.*

case class LoginFormState(
    username: String,
    password: String,
    successMessage: Option[String] = None,
    upstreamError: Option[String] = None,
    showStatus: Boolean = false
):
  lazy val usernameError: Option[String] =
    Option.when(username.isBlank)("Username cannot be blank")
  lazy val passwordError: Option[String] =
    Option.when(password.isEmpty)("Password cannot be empty")
  lazy val errorList: List[Option[String]] =
    List(usernameError, passwordError, upstreamError)
  def hasErrors: Boolean =
    errorList.exists(_.nonEmpty)
  def statusMessage: Option[Either[String, String]] =
    errorList
      .find(_.nonEmpty)
      .flatten
      .map(Left(_))
      .orElse(successMessage.map(Right(_)))
      .filter(_ => showStatus)
  def updateUsername(u: String): LoginFormState =
    copy(username = u, showStatus = false)
  def updatePassword(p: String): LoginFormState =
    copy(password = p, showStatus = false)
end LoginFormState

object LoginPage {
  final val stateVar = Var(initialState)

  private def initialState: LoginFormState = LoginFormState("", "")

  private val submitter = Observer[LoginFormState]: state =>
    if state.hasErrors then
      stateVar.update(_.copy(showStatus = true))
      println("bad beans")
    else
      println("TODO, implement backend")
      stateVar.set(initialState)
      

  def apply(): HtmlElement =
    div(
      cls := "w-full h-screen flex flex-col items-center pt-16 bg-base-200",
      loginForm()
    )

  def loginForm(): HtmlElement =
    div(
      onUnmountCallback(_ => stateVar.set(initialState)),
      cls := "card min-w-96 max-w-md bg-base-100 shadow-xl",
      div(
        cls := "card-body pt-10",
        i(cls  := "text-7xl text-center", "💋"),
        h3(cls := "text-2xl font-bold text-center pt-2 pb-4", "Sign in to your account"),
        input(
          tpe         := "text",
          placeholder := "Username",
          cls         := "input input-bordered w-full max-w-md",
          cls <-- stateVar.signal.map: s =>
            if s.usernameError.nonEmpty && s.showStatus then "border-error"
            else "",
          value <-- stateVar.signal.map(_.username),
          onInput.mapToValue --> stateVar.updater[String](_.updateUsername(_))
        ),
        input(
          tpe         := "password",
          placeholder := "Password",
          cls         := "input input-bordered w-full max-w-md",
          value <-- stateVar.signal.map(_.password),
          onInput.mapToValue --> stateVar.updater[String](_.updatePassword(_))
        ),
        div(
          cls := "card-actions justify-center pt-2 gap-y-4",
          input(
            tpe   := "submit",
            value := "Sign in",
            cls   := "btn btn-primary w-full max-w-md",
            onClick.preventDefault.mapTo(stateVar.now()) --> submitter
          ),
          a(cls := "link text-sm text-center text", "Don't have an account? Sign up")
        )
      )
    )
}
