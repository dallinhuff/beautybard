package com.dallinhuff.glamfolio.http.endpoints

import com.dallinhuff.glamfolio.domain.data.product.*
import com.dallinhuff.glamfolio.http.request.product.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*

trait ProductEndpoints extends Endpoints:
  private val productEndpoint: Endpoint[Unit, Unit, Throwable, Unit, Any] =
    baseEndpoint
      .tag("product")
      .in("product")

  private val secureProductEndpoint: Endpoint[String, Unit, Throwable, Unit, Any] =
    secureEndpoint
      .tag("product")
      .in("product")

  val createProductEndpoint: Endpoint[String, CreateProductRequest, Throwable, CreateProductResponse, Any] =
    secureProductEndpoint
      .name("create")
      .description("create a new product")
      .post
      .in(jsonBody[CreateProductRequest])
      .out(jsonBody[CreateProductResponse])

  val getAllProductsEndpoint: Endpoint[Unit, (Option[String], Option[Int]), Throwable, List[Product], Any] =
    productEndpoint
      .name("getAll")
      .description("get a page of all products")
      .get
      .in(paged)
      .out(jsonBody[List[Product]])

  val getProductByIdEndpoint: Endpoint[Unit, String, Throwable, Option[Product], Any] =
    productEndpoint
      .name("getById")
      .description("get a product by its id")
      .in("id" / path[String]("id"))
      .get
      .out(jsonBody[Option[Product]])

  val searchProductsEndpoint: Endpoint[Unit, (ProductFilter, Option[String], Option[Int]), Throwable, List[Product], Any] =
    productEndpoint
      .name("search")
      .description("search for products that match a product filter")
      .in("search")
      .post
      .in(jsonBody[ProductFilter] and paged)
      .out(jsonBody[List[Product]])
