package co.beautybard.repository

import cats.effect.{IO, Resource}
import cats.syntax.all.*
import co.beautybard.domain.data.user.{FullUser, User, UserCredentials}
import skunk.*
import skunk.codec.all.*
import skunk.syntax.all.*

import java.util.UUID

trait UserRepository[F[_]] {
  def create(user: FullUser): F[User]
  def getById(id: UUID): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
  def getCredentials(username: String): F[Option[UserCredentials]]
}

class UserRepositoryLive(override val sessionPool: Resource[IO, Session[IO]])
    extends UserRepository[IO]
    with SkunkRepository[IO, User]:
  import UserRepositoryLive.*
  override def create(newUser: FullUser): IO[User] =
    sessionPool.use(_.prepare(_create) >>= (_.unique(newUser)))
  override def getById(id: UUID): IO[Option[User]] =
    findOneBy[UUID](_getById, id)
  override def getByUsername(username: String): IO[Option[User]] =
    findOneBy[String](_getByUsername, username)
  override def getCredentials(username: String): IO[Option[UserCredentials]] =
    sessionPool.use(_.prepare(_getCredentials) >>= (_.option(username)))
end UserRepositoryLive

object UserRepositoryLive:
  private val user = (uuid, varchar(32), text.opt, text.opt).tupled.to[User]
  private val fullUser =
    (uuid, varchar(32), varchar(128), text, text.opt, text.opt).tupled.to[FullUser]
  private val userCredentials = (varchar(32), text).tupled.to[UserCredentials]

  private val _getCredentials =
    sql"SELECT username, password FROM app_user WHERE username = ${varchar(32)}"
      .query(userCredentials)

  private val _getById =
    sql"SELECT id, username, image_url, bio FROM app_user WHERE id = $uuid"
      .query(user)
    
  private val _getByUsername =
    sql"SELECT id, username, image_url, bio FROM app_user WHERE username = ${varchar(32)}"
      .query(user)

  private val _create =
    sql"""
      INSERT INTO app_user (id, username, email, password, image_url, bio)
      VALUES $fullUser
      RETURNING id, username, email, image_url, bio
    """.query(user)
