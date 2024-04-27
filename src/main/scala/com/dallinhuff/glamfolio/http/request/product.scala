package com.dallinhuff.glamfolio.http.request

import zio.json.*

object product:
  case class CreateProductRequest() derives JsonCodec
  case class CreateProductResponse() derives JsonCodec
