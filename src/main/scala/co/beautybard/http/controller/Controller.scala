package co.beautybard.http.controller

import cats.effect.IO
import sttp.tapir.server.*

trait Controller:
  def routes: List[ServerEndpoint[Any, IO]]
