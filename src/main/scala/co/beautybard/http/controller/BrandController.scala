package co.beautybard.http.controller

import co.beautybard.service.BrandService
import co.beautybard.http.endpoints.BrandEndpoints
import co.beautybard.service.BrandService
import sttp.tapir.ztapir.*
import zio.*

class BrandController private (service: BrandService) extends Controller, BrandEndpoints:
  private val create: ZServerEndpoint[Any, Any] =
    createBrandEndpoint
      .serverSecurityLogic: t =>
        ???
      .serverLogic: t =>
        ???

  private val getById: ZServerEndpoint[Any, Any] =
    getBrandByIdEndpoint
      .serverLogicSuccess: id =>
        ???

  private val getAll: ZServerEndpoint[Any, Any] =
    getAllBrandsEndpoint.serverLogic:
      service.getAll(_).either

  private val search: ZServerEndpoint[Any, Any] =
    searchBrandsEndpoint.serverLogic:
      case (filter, pageParams) =>
        service.search(filter, pageParams).either

  val routes: List[ZServerEndpoint[Any, Any]] =
    List(create, getById, getAll, search)
end BrandController

object BrandController:
  def makeZIO: URIO[BrandService, BrandController] =
    ZIO.serviceWith[BrandService](BrandController(_))
