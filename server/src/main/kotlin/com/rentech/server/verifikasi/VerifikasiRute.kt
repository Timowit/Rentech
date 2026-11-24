package com.rentech.server.verifikasi

import com.rentech.server.akun.penggunaId
import com.rentech.server.foto.terimaMultipart
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.rutVerifikasi(layanan: VerifikasiLayanan) = authenticate("app") {
    post("/verifikasi") {
        val id = call.penggunaId()
        val m = call.terimaMultipart()
        layanan.kirim(id, m.teksWajib("nik"), m.fileWajib("ktp"), m.fileWajib("swafoto"))
        call.respond(HttpStatusCode.Accepted)
    }
}
