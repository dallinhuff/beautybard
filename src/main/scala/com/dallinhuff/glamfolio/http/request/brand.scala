package com.dallinhuff.glamfolio.http.request

import com.dallinhuff.glamfolio.domain.data.brand.Brand
import zio.json.*

object brand:
  case class CreateBrandRequest(
      name: String,
      quality: Brand.Quality,
      description: Option[String] = None
  ) derives JsonCodec
  
  case class CreateBrandResponse() derives JsonCodec
