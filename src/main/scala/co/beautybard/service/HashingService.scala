package co.beautybard.service

import cats.effect.Resource
import cats.effect.kernel.Sync
import cats.syntax.all.*
import co.beautybard.config.HashingConfig

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

trait HashingService[F[_]]:
  def generate(str: String): F[String]
  def validate(test: String, hash: String): F[Boolean]

class HashingServiceLive[F[_] : Sync] private (config: HashingConfig) extends HashingService[F]:
  private val skf = SecretKeyFactory.getInstance(config.algorithm)

  override def generate(str: String): F[String] =
    for
      rng <- Sync[F].pure(new SecureRandom())
      salt <- Sync[F].delay {
        val salt = Array.ofDim[Byte](config.saltSize)
        rng.nextBytes(salt)
        salt
      }
      hashBytes <- pbkdf2(str.toCharArray, salt, config.iterations, config.hashSize)
    yield s"${config.iterations}:${toHex(salt)}:${toHex(hashBytes)}"

  override def validate(test: String, hash: String): F[Boolean] =
    for
      hashSections <- Sync[F].pure(hash.split(":"))
      nIters    = hashSections(0).toInt
      salt      = fromHex(hashSections(1))
      validHash = fromHex(hashSections(2))
      testHash <- pbkdf2(test.toCharArray, salt, nIters, config.hashSize)
    yield compareBytes(testHash, validHash)

  private def pbkdf2(msg: Array[Char], salt: Array[Byte], i: Int, nBytes: Int): F[Array[Byte]] =
    for
      keySpec <- Sync[F].pure(PBEKeySpec(msg, salt, i, nBytes * 8))
      secret  <- Sync[F].delay(skf.generateSecret(keySpec))
    yield secret.getEncoded

  private def toHex(arr: Array[Byte]): String =
    arr.map("%02X".format(_)).mkString

  private def fromHex(str: String): Array[Byte] =
    str.grouped(2).toArray.map(Integer.parseInt(_, 16).toByte)

  private def compareBytes(a: Array[Byte], b: Array[Byte]): Boolean =
    val diff =
      (0 until (a.length min b.length))
        .foldLeft(a.length ^ b.length): (acc, i) =>
          acc | (a(i) ^ b(i))
    diff == 0

object HashingServiceLive:
  def make[F[_] : Sync](config: HashingConfig): F[HashingService[F]] =
    HashingServiceLive(config).pure[F]

  def resource[F[_] : Sync](config: HashingConfig): Resource[F, HashingService[F]] =
    Resource.eval(make(config))

