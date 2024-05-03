package co.beautybard.service

import cats.effect.IO
import cats.syntax.all.*
import co.beautybard.domain.data.user.*
import co.beautybard.domain.error.{NotFoundError, UnauthorizedError}
import co.beautybard.http.request.user.RegisterRequest
import co.beautybard.repository.UserRepository

import java.util.UUID

trait UserService[F[_]] {
  def login(credentials: UserCredentials): F[UserToken]
  def register(registerRequest: RegisterRequest): F[UserToken]
  def logout(id: UUID): F[Unit]
  def getById(id: UUID): F[User]
}

class UserServiceLive private (repo: UserRepository[IO]) extends UserService[IO] {

  override def login(credentials: UserCredentials): IO[UserToken] =
    for
      canonical <- repo.getCredentials(credentials.username)
      _ <- IO.raiseUnless(canonical.contains(credentials))(UnauthorizedError(""))
      userToken <- IO.pure(UserToken(UUID.randomUUID(), "randomToken", 100L))
    yield userToken

  override def register(registerRequest: RegisterRequest): IO[UserToken] =
    for
      userToken <- IO.pure(UserToken(UUID.randomUUID(), "randomToken", 100L))
    yield userToken

  override def logout(id: UUID): IO[Unit] =
    IO.unit

  override def getById(id: UUID): IO[User] =
    for
      maybeUser <- repo.getById(id)
      user <- IO.fromOption(maybeUser)(NotFoundError("User doesn't exist"))
    yield user
}

object UserServiceLive {
  def make(repo: UserRepository[IO]): IO[UserService[IO]] =
    IO.delay(UserServiceLive(repo))
}
