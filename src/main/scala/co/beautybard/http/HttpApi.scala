package co.beautybard.http

import co.beautybard.service.BrandService
import co.beautybard.http.controller.BrandController
import co.beautybard.service.BrandService
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*
import zio.*

object HttpApi:
  private type R = BrandService

  val prometheusMetrics = PrometheusMetrics.default[Task]()

  val endpointsZIO: URIO[R, List[ZServerEndpoint[Any, Any]]] =
    for
      apiEndpoints <- makeControllers.map(_.flatMap(_.routes))
      docEndpoints = SwaggerInterpreter().fromServerEndpoints[Task](apiEndpoints, "glamfolio", "1.0.0")
      metricsEndpoint = prometheusMetrics.metricsEndpoint
    yield apiEndpoints ++ docEndpoints ++ List(metricsEndpoint)

  private def makeControllers =
    for
      brand <- BrandController.makeZIO
    yield List(brand)