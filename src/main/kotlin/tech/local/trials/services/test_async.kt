package tech.local.trials.services

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.async
import tech.local.trials.ApplicationContext
import java.io.File
import java.lang.Thread.sleep
import java.util.*

data class AsyncInput(val counter: Int)

fun tryAsync(ac: ApplicationContext): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
  async { printer(ac) }
  sleep(1000)
  call.respondText("{\"message\": \"yo!\"}", ContentType.Application.Json)
}

fun printer(ac: ApplicationContext) {
  val fileName = "${ac.config.fileStoreDirPath}/${UUID.randomUUID()}.txt"
  File(fileName).printWriter().use { out ->
    for (i in 1..100) {
      out.println("number-${i}")
      sleep(100)
    }
  }
}



