package com.dallinhuff.glamfolio.domain.data

import zio.json.*

object brand {
  case class Brand(name: String, quality: Brand.Quality, description: Option[String] = None) derives JsonCodec
  object Brand:
    enum Quality(val value: String):
      case Luxury extends Quality("luxury")
      case MidRange extends Quality("mid_range")
      case DrugStore extends Quality("drug_store")

    given JsonCodec[Quality] = JsonCodec(
      JsonEncoder[String].contramap[Quality](_.value),
      JsonDecoder.string.mapOrFail: value =>
        Quality.values
          .find(_.value == value)
          .toRight(s"$value is not a valid quality")
    )

  case class BrandFilter() derives JsonCodec
}
