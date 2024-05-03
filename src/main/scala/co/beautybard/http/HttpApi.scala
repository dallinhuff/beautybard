package co.beautybard.http

import cats.effect.{IO, Resource}
import co.beautybard.config.ApplicationConfig
import co.beautybard.service.{BrandServiceLive, UserServiceLive}
import co.beautybard.http.controller.{BrandControllerLive, UserControllerLive}
import co.beautybard.repository.{BrandRepositoryLive, SessionPool}
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.*

object HttpApi:
  val prometheusMetrics = PrometheusMetrics.default[IO]()

  val endpointsIO: IO[List[ServerEndpoint[Any, IO]]] =
    for
      apiEndpoints <- controllers.use(cs => IO.pure(cs.flatMap(_.routes)))
      docEndpoints = SwaggerInterpreter()
        .fromServerEndpoints[IO](apiEndpoints, "beautybard", "1.0.0")
      metricsEndpoint = prometheusMetrics.metricsEndpoint
    yield apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)

  private def controllers =
    for
      cfg <- ApplicationConfig.resource[IO]
      pool <- SessionPool.fromConfig[IO](cfg.db)
      repo = BrandRepositoryLive(pool)
      brandService <- Resource.eval(BrandServiceLive.make(repo))
      brandController <- Resource.eval(BrandControllerLive.make(brandService))
      userService <- Resource.eval(UserServiceLive.make)
      userController <- Resource.eval(UserControllerLive.make(userService))
    yield List(brandController, userController)
