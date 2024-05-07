package co.beautybard.http.controller

import cats.effect.IO
import co.beautybard.service.BrandService
import co.beautybard.http.endpoints.BrandEndpoints
import sttp.tapir.server.*

trait BrandController[F[_]] extends Controller[F], BrandEndpoints:
  val create: ServerEndpoint[Any, F]
  val getById: ServerEndpoint[Any, F]
  val getAll: ServerEndpoint[Any, F]
  val search: ServerEndpoint[Any, F]

class BrandControllerLive private (service: BrandService[IO]) extends BrandController[IO]:
  override val create: ServerEndpoint[Any, IO] =
    createBrandEndpoint
      .serverSecurityLogic: t =>
        ???
      .serverLogic: t =>
        ???

  override val getById: ServerEndpoint[Any, IO] =
    getBrandByIdEndpoint.serverLogic:
      service.getById(_).attempt

  override val getAll: ServerEndpoint[Any, IO] =
    getAllBrandsEndpoint.serverLogic:
      service.getAll(_).attempt

  override val search: ServerEndpoint[Any, IO] =
    searchBrandsEndpoint.serverLogic:
      case (filter, pageParams) =>
        service.search(filter, pageParams).attempt

  override val routes: List[ServerEndpoint[Any, IO]] =
    List(create, getById, getAll, search)
end BrandControllerLive

object BrandControllerLive:
  def make(service: BrandService[IO]): IO[BrandController[IO]] =
    IO.delay(BrandControllerLive(service))
