package tech.local.trials

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.readValue
import com.ryanharter.ktor.moshi.moshi
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.oauth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import io.ktor.server.jetty.JettyApplicationEngine
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.hex
import io.ktor.websocket.WebSockets
import org.slf4j.LoggerFactory
import tech.local.trials.services.tryAsync
import tech.local.trials.utils.getGoogleOauthProvider
import tech.local.trials.utils.redirect
import java.security.Security
import java.util.*
import java.util.concurrent.*


@KtorExperimentalAPI
fun routes(ac: ApplicationContext): Application.() -> Unit = {
  install(Compression)
  install(CallLogging)
  install(ContentNegotiation) {
    moshi()
  }

//  install(WebSockets)
//  install(Sessions) {
//    cookie<UserSession>("oauthSampleSessionId") {
//      val secretSignKey = hex(UUID.randomUUID().toString())
//      transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
//    }
//  }

//  install(Authentication) {
//    oauth("google-oauth") {
//      client = ac.services.httpClient
//      providerLookup = { getGoogleOauthProvider(ac) }
//      urlProvider = { redirect("/login") }
//    }
//  }

  routing {
    get("/ping") {
      call.respondText("{\"message\": \"pong\"}", ContentType.Application.Json)
    }

//    authenticate("google-oauth") {
//      route("/login") {
//        handle {
//          val principal =
//              call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
//                  ?: error("err-no-issue-with-google-oauth")
//
//          val json = ac.services.httpClient.get<String>("https://www.googleapis.com/userinfo/v2/me") {
//            header("Authorization", "Bearer ${principal.accessToken}")
//          }
//
//          val data = ac.services.mapper.default.readValue<Map<String, Any?>>(json)
//          val id = data["id"] as String?
//          if (id != null) {
//            call.sessions.set(UserSession(id))
//          }
//          call.respondRedirect("/dashboard")
//        }
//      }
//    }

    get("/test-async", tryAsync(ac))
  }
}

@KtorExperimentalAPI
object Main {
  private val log = LoggerFactory.getLogger(Main::class.java)

  @JvmStatic
  fun main(args: Array<String>) {
    try {
      initialise(args).fold({
        log.error("Cannot start application => ${it.toLogString()}")
      }, {
        log.debug("application started ...")
      })
    } catch (ex: Exception) {
      log.error(ex.message, ex)
    }
  }

  private fun initialise(args: Array<String>): Either<Error, JettyApplicationEngine> {
    Security.setProperty("crypto.policy", "unlimited")
    return ApplicationContext.configure(args[0]).flatMap { ac ->
      val server = ac.config.server
      embeddedServer(Jetty, host = server.host, port = server.port, module = routes(ac)).let {
        try {
          it.start(wait = true)
          it.right()
        } catch (e: Exception) {
          it.stop(5, 5, TimeUnit.SECONDS)
          Error(ErrorType.Configuration, "err-could-not-start-server").left()
        }
      }
    }
  }
}