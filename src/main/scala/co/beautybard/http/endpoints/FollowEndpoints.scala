package co.beautybard.http.endpoints

import co.beautybard.domain.data.follow.FollowSummary
import co.beautybard.domain.data.user.User
import co.beautybard.http.request.PageParams
import co.beautybard.http.request.follow.*
import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*

import java.util.UUID

trait FollowEndpoints extends Endpoints {
  private val followEndpoint =
    baseEndpoint
      .tag("follow")
      .in("follow")
    
  private val secureFollowEndpoint =
    secureEndpoint
      .tag("follow")
      .in("follow")
    
  val followUserEndpoint: Endpoint[String, FollowRequest, Throwable, FollowResponse, Any] =
    secureFollowEndpoint
      .name("follow")
      .description("follow a user")
      .post
      .in(jsonBody[FollowRequest])
      .out(jsonBody[FollowResponse])
    
  val unfollowUserEndpoint: Endpoint[String, UnfollowRequest, Throwable, UnfollowResponse, Any] =
    secureFollowEndpoint
      .name("unfollow")
      .description("unfollow a user")
      .delete
      .in(jsonBody[UnfollowRequest])
      .out(jsonBody[UnfollowResponse])
    
  val followSummaryEndpoint: PublicEndpoint[UUID, Throwable, FollowSummary, Any] =
    followEndpoint
      .name("summary")
      .description("get a summary of a user's followers/following")
      .get
      .in("summary" / path[UUID]("id"))
      .out(jsonBody[FollowSummary])
    
  val getFollowersEndpoint: PublicEndpoint[(UUID, PageParams), Throwable, List[User], Any] =
    followEndpoint
      .name("followers")
      .description("get a page of a user's followers")
      .get
      .in("followers" / path[UUID]("id"))
      .in(paged)
      .out(jsonBody[List[User]])

  val getFollowingEndpoint: PublicEndpoint[(UUID, PageParams), Throwable, List[User], Any] =
    followEndpoint
      .name("following")
      .description("get a page of users that a user follows")
      .get
      .in("following" / path[UUID]("id"))
      .in(paged)
      .out(jsonBody[List[User]])
}
