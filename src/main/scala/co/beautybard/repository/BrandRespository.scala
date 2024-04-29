package co.beautybard.repository

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import co.beautybard.domain.data.brand.{Brand, BrandFilter, BrandOrder}
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import fs2.Stream

import java.util.UUID

trait BrandRepository:
  def create(brand: Brand): IO[Brand]
  def getById(id: UUID): IO[Option[Brand]]
  def getAll(
      brandOrder: BrandOrder,
      last: Option[String] = None,
      limit: Option[Int] = None
  ): Stream[IO, Brand]
  def search(
      brandFilter: BrandFilter,
      brandOrder: BrandOrder,
      last: Option[String] = None,
      limit: Option[Int] = None
  ): Stream[IO, Brand]

class BrandRepositoryLive(sessionPool: Resource[IO, Session[IO]]) extends BrandRepository:
  override def create(brand: Brand): IO[Brand] =
    sessionPool.use: session =>
      session.prepare(BrandRepositoryLive.create).flatMap(_.unique(brand))

  override def getById(id: UUID): IO[Option[Brand]] =
    sessionPool.use: session =>
      session.prepare(BrandRepositoryLive.getById).flatMap(_.option(id))

  override def getAll(
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    for
      session <- Stream.resource(sessionPool)
      f = BrandRepositoryLive.getAll(brandOrder, last, limit)
      pq <- Stream.eval(session.prepare(f.fragment.query(BrandRepositoryLive.brand)))
      b <- pq.stream(f.argument, 32)
    yield b

  override def search(
      brandFilter: BrandFilter,
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    for
      session <- Stream.resource(sessionPool)
      f = BrandRepositoryLive.search(brandFilter, brandOrder, last, limit)
      pq <- Stream.eval(session.prepare(f.fragment.query(BrandRepositoryLive.brand)))
      b <- pq.stream(f.argument, 32)
    yield b

object BrandRepositoryLive:
  private val quality: Codec[Brand.Quality] =
    varchar(32).eimap(s =>
      Brand.Quality.values.find(_.value == s).toRight("Invalid brand quality")
    )(_.value)

  private val brand: Codec[Brand] =
    (uuid *: varchar(32) *: quality *: text.opt).imap(Brand.apply.tupled)((b: Brand) =>
      (b.id, b.name, b.quality, b.description)
    )

  private val create =
    sql"""
      insert into brand (name, quality, description)
      values (${varchar(32)}, $quality, ${text.opt})
      returning id, name, quality, description
    """
      .query(brand)
      .contramap[Brand]:
        case Brand(_, n, q, d) => (n, q, d)

  private val getById =
    sql"""
      select id, name, quality, description
      from brand
      where id = $uuid
    """.query(brand)

  private def getAll(by: BrandOrder, last: Option[String], limit: Option[Int]): AppliedFragment =
    val base = sql"SELECT id, name, quality, description FROM brand"

    val idGreaterThan = sql"id > $uuid"
    val nameGreaterThan = sql"name > ${varchar(32)}"

    val conds: List[AppliedFragment] = by match
      case BrandOrder.Id   => List(last.map(s => idGreaterThan(UUID.fromString(s)))).flatten
      case BrandOrder.Name => List(last.map(nameGreaterThan)).flatten

    val filterR =
      if (conds.isEmpty) AppliedFragment.empty
      else conds.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)

    val ordering = by match
      case BrandOrder.Id   => sql" ORDER BY id LIMIT $int4 "
      case BrandOrder.Name => sql" ORDER BY name LIMIT $int4 "

    base(Void) |+| filterR |+| ordering(limit.getOrElse(32))

  private def search(
      brandFilter: BrandFilter,
      by: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): AppliedFragment =
    val idGreaterThan = sql"id > $uuid".contramap[String](UUID.fromString)

    val filterConds: List[AppliedFragment] = brandFilter match
      case BrandFilter(name, quality) =>
        List(
          name.map(sql"name ILIKE ${varchar(32)}"),
          quality.map(sql"quality = ${BrandRepositoryLive.quality}")
        ).flatten

    val cursorConds: List[AppliedFragment] = by match
      case BrandOrder.Id   => List(last.map(idGreaterThan)).flatten
      case BrandOrder.Name => List(last.map(sql"name > ${varchar(32)}")).flatten

    val conds = cursorConds ++ filterConds

    val filterR =
      if (conds.isEmpty) AppliedFragment.empty
      else conds.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)

    val ordering = by match
      case BrandOrder.Id   => sql" ORDER BY id LIMIT $int4 "
      case BrandOrder.Name => sql" ORDER BY name LIMIT $int4 "

    sql"SELECT id, name, quality, description FROM brand" (Void) |+|
      filterR |+|
      ordering(limit.getOrElse(32))
