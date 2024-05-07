package co.beautybard.http.request

case class PageParams(by: String, last: Option[String], limit: Option[Int] = None)
