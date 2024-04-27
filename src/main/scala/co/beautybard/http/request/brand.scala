package co.beautybard.http.request

import co.beautybard.domain.data.brand.Brand.Quality
import co.beautybard.domain.data.brand.Brand
import zio.json.*

object brand:
  case class CreateBrandRequest(
      name: String,
      quality: Quality,
      description: Option[String] = None
  ) derives JsonCodec
  
  case class CreateBrandResponse() derives JsonCodec
