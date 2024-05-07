package co.beautybard

import cats.effect.Resource
import cats.effect.kernel.Sync
import pureconfig.*
import pureconfig.generic.derivation.default.*
import pureconfig.module.catseffect.syntax.*

object config:
  case class DbConfig(
      host: String,
      port: Int,
      user: String,
      database: String,
      password: String
  ) derives ConfigReader

  case class HashingConfig(algorithm: String, iterations: Int, saltSize: Int, hashSize: Int)
      derives ConfigReader

  case class ApplicationConfig(db: DbConfig, hashing: HashingConfig)
      derives ConfigReader

  object ApplicationConfig:
    def make[F[_]: Sync]: F[ApplicationConfig] =
      ConfigSource.default.at("beautybard").loadF[F, ApplicationConfig]()

    def resource[F[_]: Sync]: Resource[F, ApplicationConfig] =
      Resource.eval(make)
