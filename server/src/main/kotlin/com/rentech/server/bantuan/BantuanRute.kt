package com.rentech.server.bantuan

import com.rentech.server.Gagal
import com.rentech.server.akun.penggunaId
import com.rentech.server.sosial.TeksMasuk
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable data class TiketMasuk(val topik: String, val teks: String? = null)

fun Route.rutBantuan(layanan: BantuanLayanan) = authenticate("app") {
    route("/bantuan") {
        post {
            val m = call.receive<TiketMasuk>()
            call.respond(HttpStatusCode.Created, layanan.buka(call.penggunaId(), m.topik, m.teks))
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Percakapan tidak ditemukan")
            val setelah = call.request.queryParameters["setelah"]?.toIntOrNull() ?: 0
            call.respond(layanan.pesan(id, call.penggunaId(), setelah))
        }
        post("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Percakapan tidak ditemukan")
            layanan.kirim(id, call.penggunaId(), call.receive<TeksMasuk>().teks)
            call.respond(HttpStatusCode.Created)
        }
    }
}
