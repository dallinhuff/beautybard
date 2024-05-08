package co.beautybard.core

import org.scalajs.dom
import io.circe.{Encoder, Decoder}
import io.circe.parser.*
import io.circe.syntax.*

object Storage:
  def set[A : Encoder](key: String, value: A): Unit =
    dom.window.localStorage.setItem(key, value.asJson.toString)

  def get[A : Decoder](key: String): Option[A] =
    Option(dom.window.localStorage.getItem(key))
      .filter(_.nonEmpty)
      .flatMap(decode(_).toOption)

  def remove(key: String): Unit =
    dom.window.localStorage.removeItem(key)