package com.dallinhuff.glamfolio.http.controller

import com.dallinhuff.glamfolio.http.endpoints.BrandEndpoints
import com.dallinhuff.glamfolio.service.BrandService
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
      case (last, limit) => service.getAll(last, limit).either

  private val search: ZServerEndpoint[Any, Any] =
    searchBrandsEndpoint.serverLogic:
      case (filter, a, b) => service.search(filter, a, b).either

  val routes: List[ZServerEndpoint[Any, Any]] =
    List(create, getById, getAll, search)
end BrandController

object BrandController:
  def makeZIO: URIO[BrandService, BrandController] =
    ZIO.serviceWith[BrandService](BrandController(_))
