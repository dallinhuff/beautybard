package co.beautybard.service

import cats.data.OptionT
import cats.effect.IO
import cats.syntax.all.*
import co.beautybard.domain.data.user.*
import co.beautybard.domain.error.{BadRequestError, NotFoundError, UnauthorizedError}
import co.beautybard.http.request.user.RegisterRequest
import co.beautybard.repository.UserRepository

import java.util.UUID

trait UserService[F[_]] {
  def login(credentials: UserCredentials): F[UserToken]
  def register(registerRequest: RegisterRequest): F[UserToken]
  def logout(id: UUID): F[Unit]
  def getById(id: UUID): F[User]
}

class UserServiceLive private (repo: UserRepository[IO], hashing: HashingService[IO])
    extends UserService[IO] {
  override def login(credentials: UserCredentials): IO[UserToken] =
    OptionT(repo.getCredentials(credentials.username))
      .filterF(canonical => hashing.validate(credentials.password, canonical.password))
      .flatMapF(canonical => repo.getByUsername(canonical.username))
      .getOrRaise(UnauthorizedError("Invalid username/password"))
      .map(user => UserToken(user.id, "token", 0L))

  override def register(req: RegisterRequest): IO[UserToken] =
    for
      existing <- repo.getByUsername(req.username)
      _ <- IO.raiseWhen(existing.nonEmpty)(
        BadRequestError(s"Username ${req.username} is not available")
      )
      user = FullUser(
        UUID.randomUUID(),
        req.username,
        req.email,
        req.password,
        req.imageUrl,
        req.bio
      )
      created <- repo.create(user)
      userToken <- IO.pure(UserToken(created.id, "randomToken", 100L))
    yield userToken

  override def logout(id: UUID): IO[Unit] =
    IO.unit

  override def getById(id: UUID): IO[User] =
    for
      maybeUser <- repo.getById(id)
      user      <- IO.fromOption(maybeUser)(NotFoundError("User doesn't exist"))
    yield user
}

object UserServiceLive {
  def make(repo: UserRepository[IO], hashing: HashingService[IO]): IO[UserService[IO]] =
    IO.delay(UserServiceLive(repo, hashing))
}
