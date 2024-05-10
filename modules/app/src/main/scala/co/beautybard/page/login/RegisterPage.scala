package co.beautybard.page.login

import co.beautybard.component.{LoadingSpinner, TextInput, ToastMessage, Toast}
import co.beautybard.core.Session
import co.beautybard.domain.data.user.UserToken
import co.beautybard.icon.IdBadge
import com.raquo.laminar.api.L.*

case class RegisterFormState(
    username: String,
    emailAddress: String,
    password: String,
    confirmPassword: String,
    upstreamError: Option[String] = None,
    showStatus: Boolean = false,
    loading: Boolean = false
):
  lazy val usernameError: Option[String] =
    Option.when(username.isBlank)("Username cannot be blank")
  lazy val emailError: Option[String] =
    Option.when(emailAddress.isBlank)("Email address cannot be blank")
  lazy val passwordError: Option[String] =
    Option.when(password.isEmpty)("Password cannot be empty")
  lazy val confirmPasswordError: Option[String] =
    Option.when(confirmPassword != password)("Passwords must match")
  lazy val errorList: List[Option[String]] =
    List(usernameError, emailError, passwordError, confirmPasswordError, upstreamError)
  def hasErrors: Boolean =
    errorList.exists(_.nonEmpty)
  def statusMessage: Option[Either[String, String]] =
    errorList
      .find(_.nonEmpty)
      .flatten
      .map(Left(_))
      .filter(_ => showStatus)
  def updateUsername(u: String): RegisterFormState =
    copy(username = u, showStatus = false)
  def updateEmailAddress(e: String): RegisterFormState =
    copy(emailAddress = e, showStatus = false)
  def updatePassword(p: String): RegisterFormState =
    copy(password = p, showStatus = false)
  def updateConfirmPassword(p: String): RegisterFormState =
    copy(confirmPassword = p, showStatus = false)
end RegisterFormState

object RegisterPage {
  final val stateVar = Var(initialState)

  private def initialState: RegisterFormState = RegisterFormState("", "", "", "")

  private val submitter = Observer[RegisterFormState]: state =>
    if state.hasErrors then
      stateVar.update(_.copy(showStatus = true))
      println("bad beans")
    else
      stateVar.update(_.copy(loading = true))
      println("TODO, implement backend")
      //stateVar.set(initialState)

  private val messages: Signal[List[ToastMessage]] =
    stateVar.signal.map: state =>
      if !state.showStatus then Nil
      else state.errorList.flatten.map(ToastMessage("alert-error", _))

  def apply(): HtmlElement =
    div(
      cls := "w-full flex flex-col items-center pt-16",
      registerForm(),
      Toast(messages, "toast-top")
    )

  def registerForm(): HtmlElement =
    div(
      onUnmountCallback(_ => stateVar.set(initialState)),
      cls := "card shadow-xl shadow-base-100 dark:shadow-base-200 min-w-96 max-w-md bg-base-100",
      div(
        cls := "card-body pt-10",
        IdBadge("w-24 h-24 self-center"),
        h3(cls := "text-2xl font-bold text-center pb-4", "Sign up for an account"),
        TextInput(
          pHolder = "Username",
          bind = stateVar.signal.map(_.username),
          updater = stateVar.updater[String](_.updateUsername(_)),
          validator = stateVar.signal.map(s => s.usernameError.nonEmpty && s.showStatus)
        ),
        TextInput(
          inputType = "email",
          pHolder = "Email address",
          bind = stateVar.signal.map(_.emailAddress),
          updater = stateVar.updater[String](_.updateEmailAddress(_)),
          validator = stateVar.signal.map(s => s.emailError.nonEmpty && s.showStatus)
        ),
        TextInput(
          inputType = "password",
          pHolder = "Password",
          bind = stateVar.signal.map(_.password),
          updater = stateVar.updater[String](_.updatePassword(_)),
          validator = stateVar.signal.map(s => s.passwordError.nonEmpty && s.showStatus)
        ),
        TextInput(
          inputType = "password",
          pHolder = "Confirm password",
          bind = stateVar.signal.map(_.confirmPassword),
          updater = stateVar.updater[String](_.updateConfirmPassword(_)),
          validator = stateVar.signal.map(s => s.confirmPasswordError.nonEmpty && s.showStatus)
        ),
        div(
          cls := "card-actions justify-center pt-2 gap-y-4",
          button(
            cls := "btn btn-primary w-full max-w-md",
            onClick.preventDefault.mapTo(stateVar.now()) --> submitter,
            child <-- stateVar.signal.map(s =>
              if s.loading then LoadingSpinner.Medium() else "Sign up"
            )
          ),
          a(
            cls  := "link text-sm text-center text",
            href := "/login",
            "Already have an account? Log in"
          )
        )
      )
    )
}
