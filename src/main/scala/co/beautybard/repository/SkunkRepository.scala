package co.beautybard.repository

import cats.effect.{Resource, Sync}
import cats.syntax.all.*
import fs2.Stream
import skunk.{Command, Query, Session}

trait SkunkRepository[F[_]: Sync, E](sessionPool: Resource[F, Session[F]]):
  protected def findOneBy[A](query: Query[A, E], argument: A): F[Option[E]] =
    sessionPool.use(_.prepare(query) >>= (_.option(argument)))

  protected def findManyBy[A](query: Query[A, E], argument: A, chunkSize: Int = 32): Stream[F, E] =
    for
      session <- Stream.resource(sessionPool)
      preparedQuery <- Stream.eval(session.prepare(query))
      result <- preparedQuery.stream(argument, chunkSize)
    yield result

  protected def update[A](command: Command[A], argument: A): F[Unit] =
    sessionPool.use(_.prepare(command) >>= (_.execute(argument).void))
