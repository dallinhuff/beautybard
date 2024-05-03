package co.beautybard.repository

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import co.beautybard.domain.data.review.*
import fs2.Stream
import skunk.codec.all.*
import skunk.*
import skunk.syntax.all.*

import java.util.UUID

trait ReviewRepository[F[_]] {
  def create(review: Review): F[Review]
  def getById(user: UUID, product: UUID): F[Option[Review]]
  def getByUser(id: UUID): Stream[F, Review]
  def getByProduct(id: UUID): Stream[F, Review]
}

class ReviewRepositoryLive(
    sessionPool: Resource[IO, Session[IO]]
) extends ReviewRepository[IO], SkunkRepository[IO, Review](sessionPool):
  import ReviewRepositoryLive.*

  override def create(review: Review): IO[Review] =
    sessionPool.use(_.prepare(_create) >>= (_.unique(review)))
  override def getById(user: UUID, product: UUID): IO[Option[Review]] =
    for
      query <- IO.pure(_getById(user, product))
      result <- findOneBy(query.fragment.query(review), query.argument)
    yield result
  override def getByUser(id: UUID): Stream[IO, Review] =
    findManyBy(_getByUser, id)
  override def getByProduct(id: UUID): Stream[IO, Review] =
    findManyBy(_getByProduct, id)

object ReviewRepositoryLive:
  private val review =
    (uuid, uuid, int4, bool, text.opt, _text, timestamptz, timestamptz.opt).tupled.imap {
      case (u, p, rtg, w, rv, i, c, e) => Review(u, p, rtg, w, rv, i.toList, c, e)
    } { case Review(u, p, rtg, w, rv, i, c, e) =>
      (u, p, rtg, w, rv, skunk.data.Arr.fromFoldable(i), c, e)
    }

  private val base =
    sql"""
      select user_id, product_id, rating, would_buy_again, review, image_urls, created, edited
      from product_review
    """

  private val _create = sql"""
    insert into product_review
      (user_id, product_id, rating, would_buy_again, review, image_urls, created, edited)
    values
      $review
    returning
      user_id, product_id, rating, would_buy_again, review, image_urls, created, edited
  """.query(review)

  def _getById(u: UUID, p: UUID): AppliedFragment =
    base(Void) |+| sql"where user_id = $uuid and product_id = $uuid" (u, p)

  val _getByUser =
    sql"""
      select user_id, product_id, rating, would_buy_again, review, image_urls, created, edited
      from product_review
      where user_id = $uuid
    """.query(review)

  val _getByProduct =
    sql"""
      select user_id, product_id, rating, would_buy_again, review, image_urls, created, edited
      from product_review
      where product_id = $uuid
    """.query(review)
