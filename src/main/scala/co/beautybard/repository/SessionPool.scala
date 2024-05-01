package co.beautybard.repository

import cats.effect.std.Console
import cats.effect.kernel.{Temporal, Resource}
import co.beautybard.config.DbConfig
import fs2.io.net.Network
import skunk.{SSL, Session}
import natchez.Trace.Implicits.noop

object SessionPool:
  def fromConfig[F[_]: Temporal: Console: Network](
      config: DbConfig,
      numConns: Int = 24
  ): Resource[F, Resource[F, Session[F]]] =
    Session.pooled[F](
      host = config.host,
      port = config.port,
      user = config.user,
      database = config.database,
      password = Some(config.password),
      ssl = SSL.System,
      max = numConns
    )
