package com.rentech.server.sosial

import com.rentech.server.Gagal
import com.rentech.server.akun.penggunaId
import com.rentech.server.db.Barang
import com.rentech.server.db.Pesanan
import com.rentech.server.db.Ulasan
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Serializable data class UlasanMasuk(val bintang: Int, val isi: String = "")

fun Route.rutUlasan() = authenticate("app") {
    post("/pesanan/{id}/ulasan") {
        val pesananId = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")
        val penyewa = call.penggunaId()
        val u = call.receive<UlasanMasuk>()
        if (u.bintang !in 1..5) throw Gagal(HttpStatusCode.BadRequest, "Pilih 1 sampai 5 bintang")
        transaction {
            val p = Pesanan.selectAll().where { Pesanan.id eq pesananId }.firstOrNull()
            if (p == null || p[Pesanan.penyewa].value != penyewa) throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")
            if (p[Pesanan.status] !in setOf("berjalan", "selesai")) throw Gagal(HttpStatusCode.Conflict, "Ulasan bisa diberikan setelah barang diterima")
            val sudah = Ulasan.select(Ulasan.id).where { (Ulasan.pesanan eq pesananId) and (Ulasan.dari eq penyewa) }.any()
            if (sudah) throw Gagal(HttpStatusCode.Conflict, "Kamu sudah memberi ulasan untuk sewa ini")
            val pemilik = Barang.select(Barang.pemilik).where { Barang.id eq p[Pesanan.barang] }.single()[Barang.pemilik]
            Ulasan.insert {
                it[pesanan] = pesananId
                it[dari] = penyewa
                it[untuk] = pemilik
                it[bintang] = u.bintang.toShort()
                it[isi] = u.isi.trim()
            }
        }
        call.respond(HttpStatusCode.Created)
    }
}
