package co.beautybard.component

import com.raquo.laminar.api.L.*

object TextInput {
  def apply(
      inputType: "text" | "password" | "email" = "text",
      bind: Signal[String] = Signal.fromValue(""),
      updater: Observer[String] = Observer.empty[String],
      validator: Signal[Boolean] = Signal.fromValue(true),
      pHolder: String = ""
  ): Input =
    input(
      tpe := inputType,
      value <-- bind,
      onInput.mapToValue --> updater,
      placeholder := pHolder,
      cls         := "input input-bordered border-base-200 w-full max-w-md shadow-inner",
      cls <-- validator.map(if _ then "border-error" else "")
    )
}
