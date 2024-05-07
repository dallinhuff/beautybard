package co.beautybard.domain.error

class ApplicationError(msg: String) extends Throwable(msg)

case class UnauthorizedError(msg: String) extends ApplicationError(msg)
case class NotFoundError(msg: String) extends ApplicationError(msg)
case class BadRequestError(msg: String) extends ApplicationError(msg)
