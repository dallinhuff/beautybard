package co.beautybard.service

import cats.effect.IO
import co.beautybard.domain.data.user.{User, UserId, UserToken}

trait JwtService[F[_]] {
  def createToken(user: User): F[UserToken]
  def verifyToken(token: String): F[UserId]
}

class JwtServiceLive extends JwtService[IO]:
  private val ISSUER         = "beautybard.co"
  private val ALGORITHM      = ???
  private val CLAIM_USERNAME = "username"

  override def createToken(user: User): IO[UserToken] = ???
  override def verifyToken(token: String): IO[UserId] = ???
