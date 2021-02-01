package tech.local.trials

import arrow.core.Either
import arrow.core.Try
import arrow.core.some
import arrow.instances.either.monad.binding
import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.Jetty
import org.jasypt.util.text.BasicTextEncryptor
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDateTime
import java.util.*

data class ApplicationContext(
    val config: ApplicationConfig,
    val services: ApplicationServices,
    val clients: Map<APIKey, Client>
) {
  companion object {
    private val log = LoggerFactory.getLogger(ApplicationContext::class.java)

    private fun loadConfiguration(configFilePath: String): Either<Error, ApplicationConfig> =
        Try {
          DataMapper.default.readValue<ApplicationConfig>(
              File(configFilePath), ApplicationConfig::class.java
          ).apply { File(this.fileStoreDirPath).mkdirs() }
        }.toEither { e ->
          log.error("Failed to load config", e)
          Error(ErrorType.Configuration, "err-unable-to-load-config-file", cause = e.some())
        }

    private fun getEncryptor(): Either<Error, BasicTextEncryptor> =
        Try {
          // val eKey = File(eKeyFilePath).bufferedReader().use { it.readText() }
          val eKey = System.getProperty("eKey")
          BasicTextEncryptor().apply { setPassword(eKey) }
        }.toEither { e ->
          log.error("Failed to load Encryption key file", e)
          Error(ErrorType.Configuration, "err-unable-to-load-config-files", cause = e.some())
        }


    fun configure(configDir: String): Either<Error, ApplicationContext> {
      return binding<Error, ApplicationContext> {
        val config = loadConfiguration("${configDir}/config.json").bind()
        val encryptor = getEncryptor().bind()
        val services = ApplicationServices(DataMapper, encryptor)
        val clients = config.clients.map { client -> client.key to client }.toMap()
        ApplicationContext(config, services, clients)
      }.apply {
        fold(
            { log.error("err-init-application-failed :: ${it}", it.cause) },
            { log.debug("application context initialised ...") }
        )
      }
    }
  }
}

class RequestContext(
    val client: Client,
    val requestedOn: LocalDateTime = LocalDateTime.now(),
    val requestId: String = UUID.randomUUID().toString()
)
