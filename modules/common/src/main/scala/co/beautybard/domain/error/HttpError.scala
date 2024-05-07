package co.beautybard.domain.error

import sttp.model.StatusCode

case class HttpError(
  statusCode: StatusCode,
  message: String,
  cause: Throwable
) extends RuntimeException(message, cause)

object HttpError:
  def decode(tuple: (StatusCode, String)): HttpError =
    tuple._1 match
      case StatusCode.Unauthorized =>
        HttpError(tuple._1, tuple._2, UnauthorizedError(tuple._2))
      case StatusCode.NotFound =>
        HttpError(tuple._1, tuple._2, NotFoundError(tuple._2))
      case StatusCode.BadRequest =>
        HttpError(tuple._1, tuple._2, BadRequestError(tuple._2))
      case _ =>
        HttpError(tuple._1, tuple._2, ApplicationError(tuple._2))

  def encode(err: Throwable): (StatusCode, String) =
    err match
      case UnauthorizedError(msg) => (StatusCode.Unauthorized, msg)
      case NotFoundError(msg)     => (StatusCode.NotFound, msg)
      case BadRequestError(msg)   => (StatusCode.BadRequest, msg)
      case _ =>
        (StatusCode.InternalServerError, err.getMessage)
