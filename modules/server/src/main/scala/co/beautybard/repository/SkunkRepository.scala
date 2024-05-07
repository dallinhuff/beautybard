package co.beautybard.repository

import cats.effect.{Resource, Sync}
import cats.syntax.all.*
import fs2.Stream
import skunk.{AppliedFragment, Command, Decoder, Query, Session}

trait SkunkRepository[F[_]: Sync, E]:
  protected val sessionPool: Resource[F, Session[F]]

  protected def findOneBy[A](query: Query[A, E], argument: A): F[Option[E]] =
    sessionPool.use(_.prepare(query) >>= (_.option(argument)))

  protected def expectOneBy[A](query: Query[A, E], argument: A): F[E] =
    sessionPool.use(_.prepare(query) >>= (_.unique(argument)))

  protected def findManyBy(applied: AppliedFragment)(using d: Decoder[E]): Stream[F, E] =
    findManyBy(applied.fragment.query(d), applied.argument)

  protected def findManyBy[A](query: Query[A, E], argument: A, chunkSize: Int = 32): Stream[F, E] =
    Stream.resource(sessionPool)
      >>= (session => Stream.eval(session.prepare(query)))
      >>= (_.stream(argument, chunkSize))

  protected def update[A](command: Command[A], argument: A): F[Unit] =
    sessionPool.use(_.prepare(command) >>= (_.execute(argument).void))
