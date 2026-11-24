package com.rentech.server

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.http.ContentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.sql.DataSource

/** Kegagalan yang boleh dilihat klien: status HTTP beserta pesan berbahasa Indonesia. */
class Gagal(val status: HttpStatusCode, pesan: String) : RuntimeException(pesan)

@Serializable
data class PesanGagal(val pesan: String)

val jsonRentech = Json { ignoreUnknownKeys = true; encodeDefaults = true }

/** Plugin dasar dan health check. Tidak butuh database. */
fun Application.modulDasar() {
    install(ContentNegotiation) { json(jsonRentech) }
    install(StatusPages) {
        exception<Gagal> { call, e -> call.respond(e.status, PesanGagal(e.message ?: "")) }
    }
    routing {
        get("/sehat") { call.respondText("""{"status":"ok"}""", ContentType.Application.Json) }
    }
}

/** Seluruh server: modul dasar ditambah semua rute yang memakai database. */
fun Application.modul(k: Konfigurasi, db: DataSource) {
    modulDasar()
}

fun main() {
    val k = Konfigurasi.dariEnv()
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        modul(k, TODO("Task 2: buatDataSource"))
    }.start(wait = true)
}
