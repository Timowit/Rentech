package com.rentech.server.verifikasi

import com.rentech.server.Gagal
import com.rentech.server.db.Pengguna
import com.rentech.server.db.Verifikasi
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.notifikasi.NotifikasiLayanan
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.OffsetDateTime

val ALASAN_BAKU = listOf(
    "Foto KTP buram, nomor NIK tidak terbaca",
    "Wajah pada swafoto tertutup bayangan",
    "Wajah pada swafoto tidak sama dengan foto KTP",
    "Data diri tidak sesuai dengan KTP",
)

@Serializable
data class VerifikasiAntreanDto(
    val id: Int,
    val penggunaId: Int,
    val nama: String,
    val email: String,
    val noHp: String,
    val nik: String,
    val fotoKtp: String,
    val fotoSwafoto: String,
    val dikirim: String,
)

class VerifikasiLayanan(private val penyimpanan: Penyimpanan, private val notifikasi: NotifikasiLayanan) {

    /** Pengguna mengirim KTP dan swafoto. Keduanya disimpan privat. */
    fun kirim(penggunaId: Int, nik: String, ktp: ByteArray, swafoto: ByteArray) {
        val nikBersih = nik.filter { it.isDigit() }
        if (nikBersih.length != 16) throw Gagal(HttpStatusCode.BadRequest, "NIK harus 16 angka")
        transaction {
            val status = Pengguna.select(Pengguna.statusVerifikasi).where { Pengguna.id eq penggunaId }.single()[Pengguna.statusVerifikasi]
            if (status == "diperiksa") throw Gagal(HttpStatusCode.Conflict, "Dokumen kamu masih diperiksa")
            if (status == "terverifikasi") throw Gagal(HttpStatusCode.Conflict, "Identitas kamu sudah terverifikasi")
        }
        // Simpan file dulu: kalau salah satu ditolak (format/ukuran), database tidak berubah.
        val pathKtp = penyimpanan.simpan(ktp, privat = true)
        val pathSwafoto = penyimpanan.simpan(swafoto, privat = true)
        transaction {
            Verifikasi.insert {
                it[pengguna] = penggunaId
                it[fotoKtp] = pathKtp
                it[fotoSwafoto] = pathSwafoto
                it[Verifikasi.nik] = nikBersih
            }
            Pengguna.update({ Pengguna.id eq penggunaId }) { it[statusVerifikasi] = "diperiksa" }
        }
    }

    fun antrean(): List<VerifikasiAntreanDto> = transaction {
        Verifikasi.join(Pengguna, JoinType.INNER, Verifikasi.pengguna, Pengguna.id).selectAll()
            .where { Verifikasi.status eq "diperiksa" }
            .orderBy(Verifikasi.dibuat, SortOrder.ASC)
            .map {
                VerifikasiAntreanDto(
                    id = it[Verifikasi.id].value,
                    penggunaId = it[Pengguna.id].value,
                    nama = it[Pengguna.nama],
                    email = it[Pengguna.email],
                    noHp = it[Pengguna.noHp],
                    nik = it[Verifikasi.nik],
                    fotoKtp = it[Verifikasi.fotoKtp],
                    fotoSwafoto = it[Verifikasi.fotoSwafoto],
                    dikirim = it[Verifikasi.dibuat].toString(),
                )
            }
    }

    fun terima(verifikasiId: Int, adminId: Int) = putuskan(verifikasiId, adminId, "terverifikasi", emptyList())

    fun tolak(verifikasiId: Int, adminId: Int, alasan: List<String>) {
        val bersih = alasan.map { it.trim() }.filter { it.isNotEmpty() }
        if (bersih.isEmpty()) throw Gagal(HttpStatusCode.BadRequest, "Pilih minimal satu alasan penolakan")
        putuskan(verifikasiId, adminId, "ditolak", bersih)
    }

    fun pastikanAdmin(penggunaId: Int) {
        val admin = transaction {
            Pengguna.select(Pengguna.admin).where { Pengguna.id eq penggunaId }.firstOrNull()?.get(Pengguna.admin)
        }
        if (admin != true) throw Gagal(HttpStatusCode.Forbidden, "Khusus admin")
    }

    private fun putuskan(verifikasiId: Int, adminId: Int, hasil: String, alasan: List<String>) {
        pastikanAdmin(adminId)
        transaction {
            val v = Verifikasi.selectAll().where { (Verifikasi.id eq verifikasiId) and (Verifikasi.status eq "diperiksa") }.firstOrNull()
                ?: throw Gagal(HttpStatusCode.NotFound, "Verifikasi tidak ada di antrean")
            val penggunaId = v[Verifikasi.pengguna].value
            Verifikasi.update({ Verifikasi.id eq verifikasiId }) {
                it[status] = hasil
                it[Verifikasi.alasan] = alasan
                it[diperiksaOleh] = adminId
                it[diperiksaPada] = OffsetDateTime.now()
            }
            Pengguna.update({ Pengguna.id eq penggunaId }) { it[statusVerifikasi] = hasil }
            if (hasil == "terverifikasi") {
                notifikasi.kirim(penggunaId, "terverifikasi", "Identitas kamu terverifikasi", "Sekarang kamu bisa menyewa dan menyewakan barang.")
            } else {
                notifikasi.kirim(penggunaId, "ditolak", "Verifikasi belum berhasil", "Kamu bisa mengirim ulang dokumen. Tidak ada biaya.")
            }
        }
    }
}
