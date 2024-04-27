package com.dallinhuff.glamfolio.http.controller

import sttp.tapir.ztapir.*

trait Controller:
  def routes: List[ZServerEndpoint[Any, Any]]
