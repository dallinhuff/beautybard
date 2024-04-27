package co.beautybard.domain.data

import zio.json.*

import java.util.UUID

object brand {
  case class Brand(
      id: UUID,
      name: String,
      quality: Brand.Quality,
      description: Option[String] = None
  ) derives JsonCodec

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

  enum BrandOrder(val value: String):
    case Id extends BrandOrder("id")
    case Name extends BrandOrder("name")

  object BrandOrder:
    def of(s: String): Option[BrandOrder] =
      values.find(_.value == s)

  case class BrandFilter(
      name: Option[String] = None,
      quality: Option[Brand.Quality] = None
  ) derives JsonCodec
}
