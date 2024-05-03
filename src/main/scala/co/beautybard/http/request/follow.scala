package co.beautybard.http.request

import java.util.UUID

import io.circe.Codec

object follow {
  case class FollowRequest(user: UUID) derives Codec.AsObject
  case class FollowResponse(success: Boolean) derives Codec.AsObject
  case class UnfollowRequest(user: UUID) derives Codec.AsObject
  case class UnfollowResponse(success: Boolean) derives Codec.AsObject
}
