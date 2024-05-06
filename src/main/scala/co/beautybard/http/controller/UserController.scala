package co.beautybard.http.controller

import cats.effect.IO
import co.beautybard.http.endpoints.UserEndpoints
import co.beautybard.service.UserService
import sttp.tapir.server.ServerEndpoint

trait UserController[F[_]] extends Controller[F], UserEndpoints:
  val login: ServerEndpoint[Any, F]
  val register: ServerEndpoint[Any, F]
  val logout: ServerEndpoint[Any, F]
  val getById: ServerEndpoint[Any, F]

class UserControllerLive private (service: UserService[IO]) extends UserController[IO]:
  override val login: ServerEndpoint[Any, IO] =
    loginEndpoint.serverLogic(service.login(_).attempt)
  override val register: ServerEndpoint[Any, IO] =
    registerEndpoint.serverLogic(service.register(_).attempt)
  override val logout: ServerEndpoint[Any, IO] =
    logoutEndpoint
      .serverSecurityLogic: token =>
        ???
      .serverLogic: t =>
        _ => ???
  override val getById: ServerEndpoint[Any, IO] =
    getUserEndpoint.serverLogic(service.getById(_).attempt)
  override def routes: List[ServerEndpoint[Any, IO]] =
    List(login, register, logout, getById)

object UserControllerLive:
  def make(service: UserService[IO]): IO[UserController[IO]] =
    IO.delay(UserControllerLive(service))
