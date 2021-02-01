package tech.local.trials.utils

import io.ktor.application.ApplicationCall
import io.ktor.auth.OAuthServerSettings
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.request.host
import io.ktor.request.port
import tech.local.trials.ApplicationContext
import tech.local.trials.SecretString
import tech.local.trials.decrypt

fun ApplicationCall.redirect(path: String): String {
  val defaultPort = if (request.origin.scheme == "http") 80 else 443
  val hostPort = request.host()!! + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
  val protocol = request.origin.scheme
  return "$protocol://$hostPort$path"
}


fun getGoogleOauthProvider(ac: ApplicationContext) =
    OAuthServerSettings.OAuth2ServerSettings(
        name = "google",
        authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
        accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
        requestMethod = HttpMethod.Post,

        clientId = "${ac.config.google.clientId}.apps.googleusercontent.com",
        clientSecret = SecretString(ac.config.google.clientSecret).decrypt(ac.services.encryptor),
        defaultScopes = listOf("profile", "email") // no email, but gives full name, picture, and id
    )
