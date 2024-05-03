package co.beautybard.domain.data

import cats.syntax.all.*
import io.circe
import skunk.codec.all.*

import java.util.UUID

object user {
  case class User(
      id: UUID,
      username: String,
      imageUrl: Option[String],
      bio: Option[String]
  ) derives circe.Codec.AsObject

  case class FullUser(
      id: UUID,
      username: String,
      email: String,
      password: String,
      imageUrl: Option[String],
      bio: Option[String]
  ) derives circe.Codec.AsObject

  case class UserCredentials(
      username: String,
      password: String
  ) derives circe.Codec.AsObject

  case class UserToken(
      userId: UUID,
      token: String,
      expires: Long
  ) derives circe.Codec.AsObject

  given skunk.Codec[User] =
    (uuid, varchar(32), text.opt, text.opt).tupled.to[User]
  given skunk.Codec[FullUser] =
    (uuid, varchar(32), varchar(128), text, text.opt, text.opt).tupled.to[FullUser]
  given skunk.Codec[UserCredentials] =
    (varchar(32), text).tupled.to[UserCredentials]
}
