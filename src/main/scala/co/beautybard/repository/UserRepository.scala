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
  def getCredentials(username: String): F[Option[UserCredentials]]
}

class UserRepositoryLive(sessionPool: Resource[IO, Session[IO]])
    extends UserRepository[IO],
      SkunkRepository[IO, User](sessionPool):
  private val user = (uuid, varchar(32), text.opt, text.opt).tupled.to[User]
  private val fullUser =
    (uuid, varchar(32), varchar(128), text, text.opt, text.opt).tupled.to[FullUser]
  private val userCredentials = (varchar(32), text).tupled.to[UserCredentials]

  override def create(newUser: FullUser): IO[User] =
    sessionPool.use: session =>
      for
        preparedQuery <- session.prepare(sql"""
            insert into app_user (id, username, email, password, image_url, bio)
            values ($fullUser)
            returning id, username, email, image_url, bio
          """.query(user))
        result <- preparedQuery.unique(newUser)
      yield result

  override def getById(id: UUID): IO[Option[User]] =
    findOneBy[UUID](
      sql"SELECT id, username, image_url, bio from app_user where id = $uuid".query(user),
      id
    )
    
  override def getCredentials(username: String): IO[Option[UserCredentials]] =
    findOneBy[String](
      sql"SELECT username, password from app_user where username = ${varchar(32)}".query(userCredentials),
      username
    )
