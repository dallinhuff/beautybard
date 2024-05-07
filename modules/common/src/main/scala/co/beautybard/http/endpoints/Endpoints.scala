package co.beautybard.http.endpoints

import co.beautybard.domain.error.HttpError
import co.beautybard.http.request.PageParams
import sttp.tapir.*

trait Endpoints:
  val baseEndpoint: Endpoint[Unit, Unit, Throwable, Unit, Any] =
    endpoint
      .errorOut(statusCode and plainBody[String])
      .mapErrorOut[Throwable](HttpError.decode)(HttpError.encode)

  val secureEndpoint: Endpoint[String, Unit, Throwable, Unit, Any] =
    baseEndpoint.securityIn(auth.bearer[String]())

  def paged: EndpointInput[PageParams] =
    (query[String]("by") and query[Option[String]]("last") and query[Option[Int]]("limit"))
      .mapTo[PageParams]
