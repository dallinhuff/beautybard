package co.beautybard.http.request

import io.circe.Codec

object product:
  case class CreateProductRequest() derives Codec.AsObject
  case class CreateProductResponse() derives Codec.AsObject
