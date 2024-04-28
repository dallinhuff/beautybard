package co.beautybard.http

import cats.effect.IO
import co.beautybard.service.BrandService
import co.beautybard.http.controller.BrandController
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.*

object HttpApi:
  val prometheusMetrics = PrometheusMetrics.default[IO]()

  val endpointsIO: IO[List[ServerEndpoint[Any, IO]]] =
    for
      apiEndpoints <- makeControllers.map(_.flatMap(_.routes))
      docEndpoints = SwaggerInterpreter().fromServerEndpoints[IO](apiEndpoints, "glamfolio", "1.0.0")
      metricsEndpoint = prometheusMetrics.metricsEndpoint
    yield apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)

  private def makeControllers =
    for
      service <- BrandService.make
      brand <- BrandController.make(service)
    yield List(brand)