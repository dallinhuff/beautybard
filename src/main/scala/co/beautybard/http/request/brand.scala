package co.beautybard.http.request

import co.beautybard.domain.data.brand.Brand
import co.beautybard.domain.data.brand.Brand.Quality

import io.circe.*
import io.circe.syntax.*

object brand:
  case class CreateBrandRequest(
      name: String,
      quality: Quality,
      description: Option[String] = None
  ) derives Codec.AsObject
  
  case class CreateBrandResponse() derives Codec.AsObject
