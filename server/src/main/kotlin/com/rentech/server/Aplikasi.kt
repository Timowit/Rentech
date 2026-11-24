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
import com.rentech.server.akun.pasangAuth
import com.rentech.server.akun.rutAkun
import com.rentech.server.db.buatDataSource
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.foto.rutFoto
import com.rentech.server.barang.rutBarang
import com.rentech.server.notifikasi.NotifikasiLayanan
import com.rentech.server.pesanan.PesananLayanan
import com.rentech.server.pesanan.rutPesanan
import com.rentech.server.verifikasi.VerifikasiLayanan
import com.rentech.server.verifikasi.rutVerifikasi
import com.rentech.server.db.migrasi
import org.jetbrains.exposed.v1.jdbc.Database
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
        exception<io.ktor.server.plugins.BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, PesanGagal("Permintaan tidak lengkap"))
        }
    }
    routing {
        get("/sehat") { call.respondText("""{"status":"ok"}""", ContentType.Application.Json) }
    }
}

/** Seluruh server: modul dasar ditambah semua rute yang memakai database. */
fun Application.modul(k: Konfigurasi, db: DataSource) {
    Database.connect(db)
    modulDasar()
    pasangAuth(k)
    val penyimpanan = Penyimpanan(k.fotoDir)
    val notifikasi = NotifikasiLayanan()
    val verifikasi = VerifikasiLayanan(penyimpanan, notifikasi)
    val pesanan = PesananLayanan(penyimpanan, notifikasi)
    routing {
        rutAkun(k)
        rutFoto(penyimpanan)
        rutVerifikasi(verifikasi)
        rutBarang(penyimpanan)
        rutPesanan(pesanan)
    }
}

fun main() {
    val k = Konfigurasi.dariEnv()
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val db = buatDataSource(k)
    migrasi(db)
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        modul(k, db)
    }.start(wait = true)
}
