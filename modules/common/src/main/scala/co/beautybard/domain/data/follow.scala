package co.beautybard.domain.data

import io.circe.Codec

object follow {
  case class FollowSummary(followers: Int, following: Int) derives Codec.AsObject
}
