package co.beautybard.http.endpoints

import co.beautybard.domain.data.user.*
import co.beautybard.http.request.user.RegisterRequest
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

import java.util.UUID

trait UserEndpoints extends Endpoints {
  private val userEndpoint =
    baseEndpoint
      .tag("users")
      .in("users")

  private val secureUserEndpoint =
    secureEndpoint
      .tag("users")
      .in("users")
  
  val loginEndpoint: PublicEndpoint[UserCredentials, Throwable, UserToken, Any] =
    userEndpoint
      .name("login")
      .description("login and generate a JWT")
      .in("login")
      .post
      .in(jsonBody[UserCredentials])
      .out(jsonBody[UserToken])
    
  val registerEndpoint: PublicEndpoint[RegisterRequest, Throwable, UserToken, Any] =
    userEndpoint
      .name("register")
      .description("sign up a new user")
      .in("register")
      .post
      .in(jsonBody[RegisterRequest])
      .out(jsonBody[UserToken])
    
  val logoutEndpoint: Endpoint[String, Unit, Throwable, Unit, Any] =
    secureUserEndpoint
      .name("logout")
      .description("logout of current user session")
      .in("login")
      .delete
    
  val getUserEndpoint: PublicEndpoint[UUID, Throwable, User, Any] =
    userEndpoint
      .name("getUser")
      .description("get a user")
      .get
      .in(path[UUID]("id"))
      .out(jsonBody[User])
}
