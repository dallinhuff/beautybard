package co.beautybard.repository

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import co.beautybard.domain.data.brand.{Brand, BrandFilter, BrandOrder}
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import fs2.Stream

import java.util.UUID

trait BrandRepository[F[_]]:
  def create(brand: Brand): F[Brand]
  def getById(id: UUID): F[Option[Brand]]
  def getAll(
      brandOrder: BrandOrder,
      last: Option[String] = None,
      limit: Option[Int] = None
  ): Stream[F, Brand]
  def search(
      brandFilter: BrandFilter,
      brandOrder: BrandOrder,
      last: Option[String] = None,
      limit: Option[Int] = None
  ): Stream[F, Brand]

class BrandRepositoryLive(sessionPool: Resource[IO, Session[IO]])
    extends BrandRepository[IO],
      SkunkRepository[IO, Brand](sessionPool):
  import BrandRepositoryLive.*

  override def create(brand: Brand): IO[Brand] =
    sessionPool.use: session =>
      session.prepare(_create).flatMap(_.unique(brand))

  override def getById(id: UUID): IO[Option[Brand]] =
    findOneBy(_getById, id)

  override def getAll(
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    val query = _getAll(brandOrder, last, limit)
    findManyBy(query.fragment.query(brand), query.argument)

  override def search(
      brandFilter: BrandFilter,
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    val query = _search(brandFilter, brandOrder, last, limit)
    findManyBy(query.fragment.query(brand), query.argument)

object BrandRepositoryLive:
  private val quality: Codec[Brand.Quality] =
    varchar(32).eimap(s =>
      Brand.Quality.values.find(_.value == s).toRight("Invalid brand quality")
    )(_.value)

  private val brand: Codec[Brand] =
    inline def g(b: Brand) = b match
      case Brand(id, name, quality, description) => (id, name, quality, description)
    (uuid, varchar(32), quality, text.opt).tupled.imap(Brand.apply)(g)

  private val _create =
    sql"""
      insert into brand (name, quality, description)
      values (${varchar(32)}, $quality, ${text.opt})
      returning id, name, quality, description
    """
      .query(brand)
      .contramap[Brand]:
        case Brand(_, n, q, d) => (n, q, d)

  private val _getById =
    sql"""
      select id, name, quality, description
      from brand
      where id = $uuid
    """.query(brand)

  private def buildFinal(
      base: Fragment[Void],
      conds: List[AppliedFragment],
      by: BrandOrder,
      limit: Option[Int]
  ): AppliedFragment =
    val filterR =
      if (conds.isEmpty) AppliedFragment.empty
      else conds.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)

    val ordering = by match
      case BrandOrder.Id => sql" ORDER BY id LIMIT $int4 "
      case BrandOrder.Name => sql" ORDER BY name LIMIT $int4 "

    base(Void) |+| filterR |+| ordering(limit.getOrElse(32))

  private def _getAll(by: BrandOrder, last: Option[String], limit: Option[Int]): AppliedFragment =
    val base = sql"SELECT id, name, quality, description FROM brand"

    val idGreaterThan = sql"id > $uuid"
    val nameGreaterThan = sql"name > ${varchar(32)}"

    val conds: List[AppliedFragment] = by match
      case BrandOrder.Id   => List(last.map(s => idGreaterThan(UUID.fromString(s)))).flatten
      case BrandOrder.Name => List(last.map(nameGreaterThan)).flatten

    buildFinal(base, conds, by, limit)

  private def _search(
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

    buildFinal(sql"SELECT id, name, quality, description FROM brand", conds, by, limit)
