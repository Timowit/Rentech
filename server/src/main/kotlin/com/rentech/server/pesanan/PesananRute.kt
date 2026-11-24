package com.rentech.server.pesanan

import com.rentech.server.Gagal
import com.rentech.server.akun.penggunaId
import com.rentech.server.foto.terimaMultipart
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

private fun ApplicationCall.idPesanan() = parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")

fun Route.rutPesanan(layanan: PesananLayanan) = authenticate("app") {
    route("/pesanan") {
        post {
            call.respond(HttpStatusCode.Created, layanan.buat(call.penggunaId(), call.receive<PesananMasuk>()))
        }
        get("/saya") { call.respond(layanan.milikPenyewa(call.penggunaId())) }
        get("/masuk") { call.respond(layanan.masukUntukPemilik(call.penggunaId())) }
        post("/{id}/terima") { call.respond(layanan.terima(call.idPesanan(), call.penggunaId())) }
        post("/{id}/tolak") { call.respond(layanan.tolak(call.idPesanan(), call.penggunaId())) }
        post("/{id}/bukti") {
            val id = call.idPesanan()
            val penyewa = call.penggunaId()
            call.respond(layanan.kirimBukti(id, penyewa, call.terimaMultipart().fileWajib("bukti")))
        }
        post("/{id}/diterima") { call.respond(layanan.konfirmasiDiterima(call.idPesanan(), call.penggunaId())) }
    }
}
