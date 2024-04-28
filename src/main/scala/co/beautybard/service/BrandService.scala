package co.beautybard.service

import cats.effect.IO
import co.beautybard.domain.data.brand.*
import co.beautybard.domain.error.BadRequestError
import co.beautybard.http.request.PageParams
import co.beautybard.http.request.brand.CreateBrandRequest
import co.beautybard.repository.BrandRepository

import java.util.UUID

trait BrandService {
  def create(req: CreateBrandRequest): IO[Brand]
  def getById(id: UUID): IO[Option[Brand]]
  def getAll(pageParams: PageParams): IO[List[Brand]]
  def search(
    filter: BrandFilter,
    pageParams: PageParams
  ): IO[List[Brand]]
}

class BrandServiceLive (repo: BrandRepository) extends BrandService {
  private lazy val items = List(
    Brand(UUID.randomUUID, "boolin", Brand.Quality.Luxury, Some("a really nice brand")),
    Brand(UUID.randomUUID, "bussin", Brand.Quality.MidRange),
    Brand(UUID.randomUUID, "nuffin", Brand.Quality.DrugStore)
  )

  override def create(req: CreateBrandRequest): IO[Brand] =
    repo.create(Brand(UUID.randomUUID, req.name, req.quality, req.description))

  override def getById(id: UUID): IO[Option[Brand]] =
    repo.getById(id)

  override def getAll(pageParams: PageParams): IO[List[Brand]] =
    for
      PageParams(kind, last, limit) <- IO.pure(pageParams)
      maybeOrder = BrandOrder.values
        .find(_.value == kind)
        .toRight(BadRequestError(s"unknown brand order: $kind"))
      order <- IO.fromEither(maybeOrder)
      brand <- repo.getAll(order, last, limit).compile.toList
    yield brand

  override def search(
    filter: BrandFilter,
    pageParams: PageParams
  ): IO[List[Brand]] =
    for
      PageParams(kind, last, limit) <- IO.pure(pageParams)
      maybeOrder = BrandOrder.of(kind).toRight(BadRequestError("unknown brand order"))
      order <- IO.fromEither(maybeOrder)
      passingFilter =
        items.filter: i =>
          i.name.contains(filter.name.getOrElse(""))
            && (filter.quality.isEmpty || i.quality == filter.quality.get)
    yield order match
      case BrandOrder.Id => passingFilter.sortBy(_.id)
      case BrandOrder.Name => passingFilter.sortBy(_.name)
}

object BrandService {
  def make(repo: BrandRepository): IO[BrandService] =
    IO.delay(BrandServiceLive(repo))
}
