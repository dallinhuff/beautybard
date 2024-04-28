package co.beautybard.repository

import cats.effect.{IO, Resource}
import co.beautybard.config.DbConfig
import skunk.{SSL, Session}
import natchez.Trace.Implicits.noop

object SessionPool:
  def fromConfig(config: DbConfig, numConns: Int = 24): Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO](
      host = config.host,
      port = config.port,
      user = config.user,
      database = config.database,
      password = Some(config.password),
      ssl = SSL.System,
      max = numConns
    )
