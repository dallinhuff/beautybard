package com.dallinhuff.glamfolio.http.endpoints

import com.dallinhuff.glamfolio.domain.error.HttpError
import sttp.tapir.*

trait Endpoints:
  val baseEndpoint: Endpoint[Unit, Unit, Throwable, Unit, Any] =
    endpoint
      .errorOut(statusCode and plainBody[String])
      .mapErrorOut[Throwable](HttpError.decode)(HttpError.encode)

  val secureEndpoint: Endpoint[String, Unit, Throwable, Unit, Any] =
    baseEndpoint.securityIn(auth.bearer[String]())

  val paged: EndpointInput[(Option[String], Option[Int])] =
    query[Option[String]]("last") and query[Option[Int]]("limit")
