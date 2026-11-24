package com.rentech.server.sosial

import com.rentech.server.Gagal
import com.rentech.server.akun.penggunaId
import com.rentech.server.barang.barangAtau404
import com.rentech.server.db.Pesan
import com.rentech.server.jam
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

/** Sama bentuknya dengan Pesan di ChatPemilikScreen.kt, ditambah id untuk polling. */
@Serializable data class PesanDto(val id: Int, val teks: String, val waktu: String, val keluar: Boolean)

@Serializable data class TeksMasuk(val teks: String)

/**
 * Lawan bicara dalam percakapan sebuah barang.
 * Penyewa selalu bicara dengan pemilik. Pemilik memilih penyewa lewat ?dengan=.
 */
private fun ApplicationCall.lawan(barangId: Int, saya: Int): Int {
    val pemilik = barangAtau404(barangId).pemilik.id
    if (saya != pemilik) return pemilik
    return request.queryParameters["dengan"]?.toIntOrNull()
        ?: throw Gagal(HttpStatusCode.BadRequest, "Pilih penyewa yang diajak bicara")
}

fun Route.rutPesan() = authenticate("app") {
    get("/percakapan/{barangId}") {
        val barangId = call.parameters["barangId"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Barang tidak ditemukan")
        val saya = call.penggunaId()
        val lawan = call.lawan(barangId, saya)
        val setelah = call.request.queryParameters["setelah"]?.toIntOrNull() ?: 0
        val pesan = transaction {
            val antara = ((Pesan.pengirim eq saya) and (Pesan.penerima eq lawan)) or ((Pesan.pengirim eq lawan) and (Pesan.penerima eq saya))
            val hasil = Pesan.selectAll().where { (Pesan.barang eq barangId) and antara and (Pesan.id greater setelah) }
                .orderBy(Pesan.id, SortOrder.ASC)
                .map { PesanDto(it[Pesan.id].value, it[Pesan.teks], jam(it[Pesan.dibuat]), keluar = it[Pesan.pengirim].value == saya) }
            Pesan.update({ (Pesan.barang eq barangId) and (Pesan.pengirim eq lawan) and (Pesan.penerima eq saya) }) { it[dibaca] = true }
            hasil
        }
        call.respond(pesan)
    }

    post("/percakapan/{barangId}") {
        val barangId = call.parameters["barangId"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Barang tidak ditemukan")
        val saya = call.penggunaId()
        val lawan = call.lawan(barangId, saya)
        val teks = call.receive<TeksMasuk>().teks.trim()
        if (teks.isEmpty()) throw Gagal(HttpStatusCode.BadRequest, "Pesan masih kosong")
        transaction {
            Pesan.insert {
                it[barang] = barangId
                it[pengirim] = saya
                it[penerima] = lawan
                it[Pesan.teks] = teks
            }
        }
        call.respond(HttpStatusCode.Created)
    }
}
