package tech.local.trials

import arrow.core.None
import arrow.core.Option
import io.ktor.http.HttpStatusCode


class Error(
    val type: ErrorType,
    val message: String,
    val args: Map<String, Any> = mapOf(),
    val cause: Option<Throwable> = None
) {
  override fun toString() = "Fault(${type}:${message})"
  fun toLogString() = "${type}:${message}:${args}"
}

enum class ErrorType(private val code: String, private val httpStatusCode: HttpStatusCode) {
  Configuration("configuration-fault", HttpStatusCode.InternalServerError),
  ClientRequest("client-error", HttpStatusCode.BadRequest),
  Runtime("runtime-exception", HttpStatusCode.InternalServerError);

  fun getValue() = code
  fun getHttpStatusCode() = httpStatusCode
}