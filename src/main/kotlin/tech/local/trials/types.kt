package tech.local.trials

import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.HttpClient
import org.jasypt.util.text.BasicTextEncryptor

typealias APIKey = String
typealias APISecret = String

inline class SecretString(val encryptedValue: String) {
  constructor(message: String, encryptor: BasicTextEncryptor) : this(encryptor.encrypt(message))
}

fun String.getEncryptor() = BasicTextEncryptor().apply { setPassword(this@getEncryptor) }

fun SecretString.decrypt(encryptor: BasicTextEncryptor) = encryptor.decrypt(encryptedValue)


data class Client(val name: String, val handle: String, val key: APIKey, val secret: APISecret)

data class ServerConfiguration(val host: String = "0.0.0.0", val port: Int = 6969)

data class GoogleOAuthConfig(val clientId: String, val clientSecret: String)

data class ApplicationConfig(
    val server: ServerConfiguration = ServerConfiguration(),

    @JsonProperty("file-store-dir-path")
    val fileStoreDirPath: String,

    val google: GoogleOAuthConfig,

    val clients: List<Client>
)

data class ApplicationServices(
    val mapper: DataMapper,
    val encryptor: BasicTextEncryptor
)

object DataMapper {
  private val module = KotlinModule()

  val default: ObjectMapper = ObjectMapper()
      .registerModule(module)
      .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
      .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
      .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
}

enum class ApiVersion(val str: String) {
  OneOh("v1.0");

  companion object {
    private val map = values().map { it.str to it }.toMap()

    operator fun invoke(str: String) = map[str]?.right()
        ?: Error(ErrorType.ClientRequest, "err-invalid-version", mapOf("input-version" to str)).left()
  }
}


class UserSession(val userId: String)