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

class BrandRepositoryLive(override val sessionPool: Resource[IO, Session[IO]])
    extends BrandRepository[IO]
    with SkunkRepository[IO, Brand]:
  import BrandRepositoryLive.*
  override def create(brand: Brand): IO[Brand] =
    expectOneBy(_create, brand)
  override def getById(id: UUID): IO[Option[Brand]] =
    findOneBy(_getById, id)
  override def getAll(
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    findManyBy(_getAll(brandOrder, last, limit))(using BrandCodec.brand)
  override def search(
      brandFilter: BrandFilter,
      brandOrder: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): Stream[IO, Brand] =
    findManyBy(_search(brandFilter, brandOrder, last, limit))(using BrandCodec.brand)
end BrandRepositoryLive

object BrandRepositoryLive:
  import BrandCodec.*
  private val base = "id, name, quality, image_url, description"

  private val _create =
    sql"""
      INSERT INTO brand (name, quality, image_url, description)
      VALUES ($name, $quality, $image_url, $description)
      RETURNING #$base
    """
      .query(brand)
      .contramap[Brand]:
        case Brand(_, n, q, i, d) => (n, q, i, d)

  private val _getById =
    sql"SELECT #$base FROM brand WHERE id = $uuid".query(brand)

  private def buildFinal(
      conds: List[AppliedFragment],
      by: BrandOrder,
      limit: Option[Int]
  ): AppliedFragment =
    sql"select #$base from brand" (Void)
      |+| (if conds.isEmpty then AppliedFragment.empty
           else conds.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty))
      |+| sql" ORDER BY #${by.value} LIMIT $int4" (limit.getOrElse(32))

  private def _getAll(by: BrandOrder, last: Option[String], limit: Option[Int]): AppliedFragment =
    val conds: List[AppliedFragment] = by match
      case BrandOrder.Id   => List(last.map(sql"id > $uuid".contramap(UUID.fromString))).flatten
      case BrandOrder.Name => List(last.map(sql"name > $name")).flatten
    buildFinal(conds, by, limit)

  private def cursorFilter(by: BrandOrder, last: Option[String]): Option[AppliedFragment] =
    by match
      case BrandOrder.Id   => last.map(sql"id > $uuid".contramap(UUID.fromString))
      case BrandOrder.Name => last.map(sql"name > $name")

  private def _search(
      brandFilter: BrandFilter,
      by: BrandOrder,
      last: Option[String],
      limit: Option[Int]
  ): AppliedFragment =
    val conds: List[AppliedFragment] =
      List(
        cursorFilter(by, last),
        brandFilter.name.map(sql"name ILIKE $name"),
        brandFilter.quality.map(sql"quality = $quality")
      ).flatten

    buildFinal(conds, by, limit)

object BrandCodec:
  // columns
  val id: Codec[UUID]     = uuid
  val name: Codec[String] = varchar(64)
  val quality: Codec[Brand.Quality] =
    varchar(32).eimap(s =>
      Brand.Quality.values.find(_.value == s).toRight("Invalid brand quality")
    )(_.value)
  val image_url: Codec[Option[String]]   = text.opt
  val description: Codec[Option[String]] = text.opt

  // whole thing
  val brand: Codec[Brand] = (id, name, quality, image_url, description).tupled.to[Brand]
