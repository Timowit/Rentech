package com.rentech.server.akun

import at.favre.lib.crypto.bcrypt.BCrypt
import com.rentech.server.Gagal
import com.rentech.server.Konfigurasi
import com.rentech.server.db.Pengguna
import com.rentech.server.db.Verifikasi
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.auth.authenticate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

const val KODE_OTP_DEV = "123456"
private const val BIAYA_BCRYPT = 12

@Serializable data class DaftarMasuk(val nama: String, val email: String, val noHp: String, val sandi: String)
@Serializable data class OtpMasuk(val email: String, val kode: String)
@Serializable data class MasukMasuk(val email: String, val sandi: String)
@Serializable data class UbahSaya(val peranAktif: String? = null)
@Serializable data class IdKeluar(val id: Int)
@Serializable data class TokenKeluar(val token: String)

@Serializable
data class PenggunaDto(
    val id: Int,
    val nama: String,
    val email: String,
    val noHp: String,
    val foto: String?,
    val peranAktif: String,
    val statusVerifikasi: String,
    val alasanDitolak: List<String>,
)

fun hashSandi(sandi: String): String = BCrypt.withDefaults().hashToString(BIAYA_BCRYPT, sandi.toCharArray())
fun cocokSandi(sandi: String, hash: String): Boolean = BCrypt.verifyer().verify(sandi.toCharArray(), hash).verified

private fun idDariEmail(email: String): Pair<Int, String>? =
    Pengguna.select(Pengguna.id, Pengguna.sandiHash).where { Pengguna.email eq email.trim().lowercase() }
        .firstOrNull()?.let { it[Pengguna.id].value to it[Pengguna.sandiHash] }

fun penggunaDto(id: Int): PenggunaDto = transaction {
    val p = Pengguna.selectAll().where { Pengguna.id eq id }.firstOrNull()
        ?: throw Gagal(HttpStatusCode.Unauthorized, "Akun tidak ditemukan")
    val alasan = if (p[Pengguna.statusVerifikasi] == "ditolak") {
        Verifikasi.select(Verifikasi.alasan)
            .where { (Verifikasi.pengguna eq id) and (Verifikasi.status eq "ditolak") }
            .orderBy(Verifikasi.id, SortOrder.DESC).firstOrNull()?.get(Verifikasi.alasan).orEmpty()
    } else emptyList()
    PenggunaDto(
        id = id,
        nama = p[Pengguna.nama],
        email = p[Pengguna.email],
        noHp = p[Pengguna.noHp],
        foto = p[Pengguna.foto]?.let { "/foto/$it" },
        peranAktif = p[Pengguna.peranAktif],
        statusVerifikasi = p[Pengguna.statusVerifikasi],
        alasanDitolak = alasan,
    )
}

fun Route.rutAkun(k: Konfigurasi) = route("/akun") {
    post("/daftar") {
        val d = call.receive<DaftarMasuk>()
        val email = d.email.trim().lowercase()
        if (d.nama.isBlank() || !email.contains('@') || d.noHp.isBlank()) throw Gagal(HttpStatusCode.BadRequest, "Lengkapi nama, email, dan nomor HP")
        if (d.sandi.length < 8) throw Gagal(HttpStatusCode.BadRequest, "Kata sandi minimal 8 karakter")
        val id = transaction {
            if (idDariEmail(email) != null) throw Gagal(HttpStatusCode.Conflict, "Email sudah terdaftar")
            Pengguna.insertAndGetId {
                it[nama] = d.nama.trim()
                it[Pengguna.email] = email
                it[noHp] = d.noHp.trim()
                it[sandiHash] = hashSandi(d.sandi)
            }.value
        }
        call.respond(HttpStatusCode.Created, IdKeluar(id))
    }

    post("/otp") {
        val o = call.receive<OtpMasuk>()
        // Belum ada pengirim SMS: di luar mode pengembangan, OTP selalu gagal.
        val id = transaction { idDariEmail(o.email)?.first }
        if (!k.otpDev || o.kode != KODE_OTP_DEV || id == null) throw Gagal(HttpStatusCode.Unauthorized, "Kode OTP salah")
        call.respond(TokenKeluar(buatToken(k, id)))
    }

    post("/masuk") {
        val m = call.receive<MasukMasuk>()
        val akun = transaction { idDariEmail(m.email) }
        if (akun == null || !cocokSandi(m.sandi, akun.second)) throw Gagal(HttpStatusCode.Unauthorized, "Email atau kata sandi salah")
        call.respond(TokenKeluar(buatToken(k, akun.first)))
    }

    authenticate("app") {
        get("/saya") { call.respond(penggunaDto(call.penggunaId())) }

        patch("/saya") {
            val id = call.penggunaId()
            // Hanya peranAktif yang bisa diubah. Kolom admin tidak pernah dibaca dari permintaan.
            val u = call.receive<UbahSaya>()
            u.peranAktif?.let { peran ->
                if (peran !in setOf("penyewa", "pemilik")) throw Gagal(HttpStatusCode.BadRequest, "Peran tidak dikenal")
                transaction { Pengguna.update({ Pengguna.id eq id }) { it[peranAktif] = peran } }
            }
            call.respond(penggunaDto(id))
        }
    }
}
