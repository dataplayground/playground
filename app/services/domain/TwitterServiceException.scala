package services.domain

case class TwitterServiceException(message: String, cause: Throwable)
  extends RuntimeException(message, cause)
