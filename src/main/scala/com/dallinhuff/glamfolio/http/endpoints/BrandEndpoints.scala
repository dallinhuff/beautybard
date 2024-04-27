package com.dallinhuff.glamfolio.http.endpoints

import com.dallinhuff.glamfolio.domain.data.brand.*
import com.dallinhuff.glamfolio.http.request.brand.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*

import java.util.UUID

trait BrandEndpoints extends Endpoints {
  private val brandEndpoint: PublicEndpoint[Unit, Throwable, Unit, Any] =
    baseEndpoint
      .tag("brands")
      .in("brands")

  private val secureBrandEndpoint: Endpoint[String, Unit, Throwable, Unit, Any] =
    secureEndpoint
      .tag("brands")
      .in("brands")

  val createBrandEndpoint: Endpoint[String, CreateBrandRequest, Throwable, Brand, Any] =
    secureBrandEndpoint
      .name("create")
      .description("create a new brand")
      .post
      .in(jsonBody[CreateBrandRequest])
      .out(jsonBody[Brand])

  val getAllBrandsEndpoint: PublicEndpoint[(Option[String], Option[Int]), Throwable, List[Brand], Any] =
    brandEndpoint
      .name("getAll")
      .description("get all brands")
      .get
      .in(paged)
      .out(jsonBody[List[Brand]])

  val getBrandByIdEndpoint: PublicEndpoint[UUID, Throwable, Option[Brand], Any] =
    brandEndpoint
      .name("getById")
      .description("get a brand by its id")
      .in("id" / path[UUID]("id"))
      .get
      .out(jsonBody[Option[Brand]])

  val searchBrandsEndpoint: PublicEndpoint[(BrandFilter, Option[String], Option[Int]), Throwable, List[Brand], Any] =
    brandEndpoint
      .name("search")
      .description("search for brands that match a filter")
      .in("search")
      .post
      .in(jsonBody[BrandFilter] and paged)
      .out(jsonBody[List[Brand]])
}
