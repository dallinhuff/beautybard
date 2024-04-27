package co.beautybard.service

import co.beautybard.domain.data.brand.*
import co.beautybard.domain.error.BadRequestError
import co.beautybard.http.request.PageParams
import co.beautybard.http.request.brand.CreateBrandRequest
import zio.*

import java.util.UUID

trait BrandService {
  def create(req: CreateBrandRequest): Task[Brand]
  def getById(id: UUID): Task[Option[Brand]]
  def getAll(pageParams: PageParams): Task[List[Brand]]
  def search(
      filter: BrandFilter,
      pageParams: PageParams
  ): Task[List[Brand]]
}

class BrandServiceLive extends BrandService {
  private lazy val items = List(
    Brand(UUID.randomUUID, "boolin", Brand.Quality.Luxury, Some("a really nice brand")),
    Brand(UUID.randomUUID, "bussin", Brand.Quality.MidRange),
    Brand(UUID.randomUUID, "nuffin", Brand.Quality.DrugStore)
  )

  override def create(req: CreateBrandRequest): Task[Brand] =
    ZIO.succeed(Brand(UUID.randomUUID, req.name, req.quality, req.description))

  override def getById(id: UUID): Task[Option[Brand]] =
    ZIO.some(Brand(UUID.randomUUID, "boolin", Brand.Quality.Luxury, Some("a really nice brand")))

  override def getAll(pageParams: PageParams): Task[List[Brand]] =
    for
      PageParams(kind, last, limit) <- ZIO.succeed(pageParams)
      maybeOrder = BrandOrder.values
        .find(_.value == kind)
        .toRight(new RuntimeException("bad order"))
      order <- ZIO.fromEither(maybeOrder)
    yield order match
      case BrandOrder.Id   => items.sortBy(_.id)
      case BrandOrder.Name => items.sortBy(_.name)

  override def search(
      filter: BrandFilter,
      pageParams: PageParams
  ): Task[List[Brand]] =
    for
      PageParams(kind, last, limit) <- ZIO.succeed(pageParams)
      maybeOrder = BrandOrder.of(kind).toRight(BadRequestError("unknown brand order"))
      order <- ZIO.fromEither(maybeOrder)
      passingFilter =
        items.filter: i =>
          i.name.contains(filter.name.getOrElse(""))
          && (filter.quality.isEmpty || i.quality == filter.quality.get)
    yield order match
      case BrandOrder.Id => passingFilter.sortBy(_.id)
      case BrandOrder.Name => passingFilter.sortBy(_.name)
}

object BrandService {
  val live: ZLayer[Any, Nothing, BrandService] =
    ZLayer(ZIO.succeed(new BrandServiceLive))
}
