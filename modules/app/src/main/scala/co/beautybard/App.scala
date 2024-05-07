package co.beautybard

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}

@main
def App(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  def appElement(): Element = div(
    cls := "flex min-h-full flex-1 flex-col justify-center px-6 py-12 lx:px-8",
    loginHeader(),
    div(cls := "mt-10 sm:mx-auto sm:w-full sm:max-w-sm", loginForm())
  )

  def loginHeader(): Element = div(
    cls := "sm:mx-auto sm:w-full sm:max-w-sm",
    img(
      cls := "mx-auto h-10 w-auto",
      src := "https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=600",
      alt := ""
    ),
    h2(
      cls := "mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900",
      "Sign in to your account"
    )
  )

  def loginForm(): Element = form(
    cls    := "space-y-6",
    action := "#",
    method := "POST",
    div(
      cls := "flex flex-col gap-y-4",
      emailInput(),
      passwordInput(),
      div(
        button(
          tpe := "submit",
          cls := "flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600",
          "Sign in"
        )
      )
    )
  )

  def emailInput(): Element =
    div(
      label(cls := "block text-sm font-medium leading-6 text-gray-900", "Email address"),
      div(
        cls := "mt-2",
        input(
          tpe          := "email",
          autoComplete := "email",
          required     := true,
          cls := "block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
        )
      )
    )

  def passwordInput(): Element =
    div(
      div(
        cls := "flex items-center justify-between",
        label(cls := "block text-sm font-medium leading-6 text-gray-900", "Password"),
        div(
          cls := "text-sm",
          a(
            href := "#",
            cls  := "font-semibold text-indigo-600 hover:text-indigo-500",
            "Forgot password?"
          )
        )
      ),
      div(
        cls := "mt-2",
        input(
          tpe          := "password",
          autoComplete := "current-password",
          required     := true,
          cls := "block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
        )
      )
    )
end Main
