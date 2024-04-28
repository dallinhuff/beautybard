package co.beautybard

import cats.effect.{ExitCode, IO, IOApp, Resource}
import co.beautybard.http.HttpApi
import com.comcast.ip4s.{Host, Port, port}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =

    val serverOptions: Http4sServerOptions[IO] =
      Http4sServerOptions
        .customiseInterceptors[IO]
        .metricsInterceptor(HttpApi.prometheusMetrics.metricsInterceptor())
        .options

    val port = sys.env
      .get("HTTP_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    val server = for
      endpoints <- Resource.eval(HttpApi.endpointsIO)
      routes = Http4sServerInterpreter[IO](serverOptions).toRoutes(endpoints)
      s <- EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString("localhost").get)
        .withPort(port)
        .withHttpApp(Router("/" -> routes).orNotFound)
        .build
    yield s


    server.use { server =>
        for {
          _ <- IO.println(s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit.")
          _ <- IO.readLine
        } yield ()
      }
      .as(ExitCode.Success)
