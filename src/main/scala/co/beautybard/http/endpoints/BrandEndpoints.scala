package co.beautybard.http.endpoints

import co.beautybard.domain.data.brand.*
import co.beautybard.http.request.PageParams
import co.beautybard.http.request.brand.*
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

import java.util.UUID

trait BrandEndpoints extends Endpoints:
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

  val getAllBrandsEndpoint: PublicEndpoint[PageParams, Throwable, List[Brand], Any] =
    brandEndpoint
      .name("getAll")
      .description("get all brands ordered")
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

  val searchBrandsEndpoint: PublicEndpoint[(BrandFilter, PageParams), Throwable, List[Brand], Any] =
    brandEndpoint
      .name("search")
      .description("search for brands that match a filter")
      .in("search")
      .post
      .in(jsonBody[BrandFilter] and paged)
      .out(jsonBody[List[Brand]])
