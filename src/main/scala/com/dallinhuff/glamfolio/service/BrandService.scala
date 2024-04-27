package com.dallinhuff.glamfolio.service

import com.dallinhuff.glamfolio.domain.data.brand.{Brand, BrandFilter}
import com.dallinhuff.glamfolio.http.request.brand.CreateBrandRequest
import zio.*

import java.util.UUID

trait BrandService {
  def create(req: CreateBrandRequest): Task[Brand]
  def getById(id: UUID): Task[Option[Brand]]
  def getAll(last: Option[String] = None, limit: Option[Int] = None): Task[List[Brand]]
  def search(
      filter: BrandFilter,
      last: Option[String] = None,
      limit: Option[Int] = None
  ): Task[List[Brand]]
}

class BrandServiceLive extends BrandService {
  override def create(req: CreateBrandRequest): Task[Brand] =
    ZIO.succeed:
      req match
        case CreateBrandRequest(name, quality, description) =>
          Brand(name, quality, description)

  override def getById(id: UUID): Task[Option[Brand]] =
    ZIO.some(Brand("boolin", Brand.Quality.Luxury, Some("a really nice brand")))

  override def getAll(last: Option[String], limit: Option[RuntimeFlags]): Task[List[Brand]] =
    ZIO.succeed:
      List(
        Brand("boolin", Brand.Quality.Luxury, Some("a really nice brand")),
        Brand("bussin", Brand.Quality.MidRange),
        Brand("nuffin", Brand.Quality.DrugStore)
      )

  override def search(
      filter: BrandFilter,
      last: Option[String],
      limit: Option[RuntimeFlags]
  ): Task[List[Brand]] = ???
}

object BrandService {
  val live: ZLayer[Any, Nothing, BrandService] =
    ZLayer(ZIO.succeed(new BrandServiceLive))
}
