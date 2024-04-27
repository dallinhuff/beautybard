package co.beautybard

import co.beautybard.http.HttpApi
import co.beautybard.service.BrandService
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}
import zio.http.Server
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault:

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = SLF4J.slf4j(LogFormat.default)

  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] =
    val port = sys.env.get("HTTP_PORT").flatMap(_.toIntOption).getOrElse(8080)
    
    val program = for
      endpoints <- HttpApi.endpointsZIO
      serverOptions = ZioHttpServerOptions
        .customiseInterceptors
        .metricsInterceptor(HttpApi.prometheusMetrics.metricsInterceptor())
        .options
      actualPort <- Server.install(
        ZioHttpInterpreter(serverOptions)
          .toHttp(endpoints)
      )
      _ <- Console.printLine(
        s"Go to http://localhost:$actualPort/docs to open SwaggerUI. Press ENTER key to exit."
      )
      _ <- Console.readLine
    yield ()

    program
      .provide(
        ZLayer.succeed(Server.Config.default.port(port)),
        Server.live,
        BrandService.live
      )
      .exitCode
