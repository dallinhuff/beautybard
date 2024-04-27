package co.beautybard.domain.data

import zio.json.*

object product {
  case class Product() derives JsonCodec
  case class ProductFilter() derives JsonCodec
}
