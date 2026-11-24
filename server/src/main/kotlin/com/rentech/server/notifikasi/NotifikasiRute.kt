package com.rentech.server.notifikasi

import com.rentech.server.Gagal
import com.rentech.server.ZONA_WIB
import com.rentech.server.akun.penggunaId
import com.rentech.server.db.Notifikasi
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDate

/** waktu berupa ISO-8601. Aplikasi yang mengubahnya jadi "10 menit lalu". */
@Serializable
data class NotifikasiDto(
    val id: Int,
    val jenis: String,
    val judul: String,
    val isi: String,
    val waktu: String,
    val hariIni: Boolean,
    val tujuan: String,
    val dibaca: Boolean,
)

fun Route.rutNotifikasi() = authenticate("app") {
    route("/notifikasi") {
        get {
            val saya = call.penggunaId()
            val hariIni = LocalDate.now(ZONA_WIB)
            val daftar = transaction {
                Notifikasi.selectAll().where { Notifikasi.pengguna eq saya }
                    .orderBy(Notifikasi.dibuat to SortOrder.DESC, Notifikasi.id to SortOrder.DESC)
                    .map {
                        val dibuat = it[Notifikasi.dibuat]
                        NotifikasiDto(
                            id = it[Notifikasi.id].value,
                            jenis = it[Notifikasi.jenis],
                            judul = it[Notifikasi.judul],
                            isi = it[Notifikasi.isi],
                            waktu = dibuat.toString(),
                            hariIni = dibuat.atZoneSameInstant(ZONA_WIB).toLocalDate() == hariIni,
                            tujuan = it[Notifikasi.tujuan],
                            dibaca = it[Notifikasi.dibaca],
                        )
                    }
            }
            call.respond(daftar)
        }

        post("/dibaca-semua") {
            val saya = call.penggunaId()
            transaction { Notifikasi.update({ Notifikasi.pengguna eq saya }) { it[dibaca] = true } }
            call.respond(HttpStatusCode.OK)
        }

        post("/{id}/dibaca") {
            val saya = call.penggunaId()
            val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Notifikasi tidak ditemukan")
            val berubah = transaction { Notifikasi.update({ (Notifikasi.id eq id) and (Notifikasi.pengguna eq saya) }) { it[dibaca] = true } }
            if (berubah == 0) throw Gagal(HttpStatusCode.NotFound, "Notifikasi tidak ditemukan")
            call.respond(HttpStatusCode.OK)
        }
    }
}
