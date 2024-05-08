package co.beautybard.core

import scala.scalajs.js.Date

import co.beautybard.domain.data.user.UserToken
import com.raquo.laminar.api.L.*

object Session:
  private val stateName = "userState"

  val userState = Var(Option.empty[UserToken])

  def isActive: Boolean =
    loadUserState()
    userState.now().isDefined

  def getUserState: Option[UserToken] =
    loadUserState()
    userState.now()

  def setUserState(token: UserToken): Unit =
    userState.set(Option(token))
    Storage.set(stateName, token)

  def loadUserState(): Unit =
    // clear expired token
    Storage
      .get[UserToken](stateName)
      .filter(_.expires * 1000 <= new Date().getTime())
      .foreach(_ => Storage.remove(stateName))

    // set existing token
    userState.set(Storage.get[UserToken](stateName))

  def clearUserState(): Unit =
    userState.set(Option.empty)
    Storage.remove(stateName)
