package co.beautybard.http.controller

import sttp.tapir.server.ServerEndpoint

trait Controller[F[_]]:
  def routes: List[ServerEndpoint[Any, F]]
