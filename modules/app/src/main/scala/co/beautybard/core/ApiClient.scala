package co.beautybard.core

import co.beautybard.http.endpoints.*
import sttp.tapir.*
import sttp.client3.*

trait ApiClient {
  val brand: BrandEndpoints = new BrandEndpoints {}
  val follow: FollowEndpoints = new FollowEndpoints {}
  val product: ProductEndpoints = new ProductEndpoints {}
  val review: ReviewEndpoints = new ReviewEndpoints {}
  val user: UserEndpoints = new UserEndpoints {}
  
  def endpointRequest[I, E <: Throwable, O](
      endpoint: PublicEndpoint[I, E, O, Any]
  ): I => Request[Either[E, O], Any]
  
  def secureEndpointRequest[I, E <: Throwable, O](
      endpoint: Endpoint[String, I, E, O, Any]
  ): String => I => Request[Either[E, O], Any]
}
