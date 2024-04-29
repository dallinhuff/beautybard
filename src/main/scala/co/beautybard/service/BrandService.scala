package co.beautybard.service

import cats.effect.{IO, Resource}
import co.beautybard.domain.data.brand.*
import co.beautybard.http.request.PageParams
import co.beautybard.http.request.brand.CreateBrandRequest
import co.beautybard.repository.BrandRepository

import java.util.UUID

trait BrandService:
  def create(req: CreateBrandRequest): IO[Brand]
  def getById(id: UUID): IO[Option[Brand]]
  def getAll(pageParams: PageParams): IO[List[Brand]]
  def search(
      filter: BrandFilter,
      pageParams: PageParams
  ): IO[List[Brand]]

class BrandServiceLive private (repo: BrandRepository) extends BrandService:
  override def create(req: CreateBrandRequest): IO[Brand] =
    repo.create(Brand(UUID.randomUUID, req.name, req.quality, req.description))

  override def getById(id: UUID): IO[Option[Brand]] =
    repo.getById(id)

  override def getAll(pageParams: PageParams): IO[List[Brand]] =
    for
      PageParams(kind, last, limit) <- IO.pure(pageParams)

      order <- IO.fromEither(BrandOrder.of(kind))
      brand <- repo.getAll(order, last, limit).compile.toList
    yield brand

  override def search(
      filter: BrandFilter,
      pageParams: PageParams
  ): IO[List[Brand]] =
    for
      PageParams(kind, last, limit) <- IO.pure(pageParams)
      order <- IO.fromEither(BrandOrder.of(kind))
      f = filter.copy(name = filter.name.map(_ :+ '%'))
      brand <- repo.search(f, order, last, limit).compile.toList
    yield brand

object BrandServiceLive:
  def make(repo: BrandRepository): IO[BrandService] =
    IO.delay(BrandServiceLive(repo))

  def resource(repo: Resource[IO, BrandRepository]): Resource[IO, BrandService] =
    repo.evalMap(make)
