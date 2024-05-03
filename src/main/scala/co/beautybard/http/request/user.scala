package co.beautybard.http.request

import io.circe.Codec

import java.util.UUID

object user {
  case class RegisterRequest(
      id: UUID,
      username: String,
      email: String,
      password: String,
      confirmPassword: String,
      imageUrl: Option[String],
      bio: Option[String]
  ) derives Codec.AsObject
}
