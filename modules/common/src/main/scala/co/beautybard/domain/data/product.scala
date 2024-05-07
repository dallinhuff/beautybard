package co.beautybard.domain.data

import io.circe.Codec

object product {
  case class Product() derives Codec.AsObject
  case class ProductFilter() derives Codec.AsObject
}
