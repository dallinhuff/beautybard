package co.beautybard.http.controller

import cats.effect.IO
import co.beautybard.service.BrandService
import co.beautybard.http.endpoints.BrandEndpoints
import sttp.tapir.server.*

class BrandController private (service: BrandService) extends Controller, BrandEndpoints:
  private val create: ServerEndpoint[Any, IO] =
    createBrandEndpoint
      .serverSecurityLogic: t =>
        ???
      .serverLogic: t =>
        ???

  private val getById: ServerEndpoint[Any, IO] =
    getBrandByIdEndpoint.serverLogic:
      service.getById(_).attempt

  private val getAll: ServerEndpoint[Any, IO] =
    getAllBrandsEndpoint.serverLogic:
      service.getAll(_).attempt

  private val search: ServerEndpoint[Any, IO] =
    searchBrandsEndpoint.serverLogic:
      case (filter, pageParams) =>
        service.search(filter, pageParams).attempt

  val routes: List[ServerEndpoint[Any, IO]] =
    List(create, getById, getAll, search)
end BrandController

object BrandController:
  def make(service: BrandService): IO[BrandController] =
    IO.delay(BrandController(service))
