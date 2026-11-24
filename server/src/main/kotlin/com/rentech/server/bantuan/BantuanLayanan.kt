package com.rentech.server.bantuan

import com.rentech.server.Gagal
import com.rentech.server.akun.pastikanAdmin
import com.rentech.server.db.Pengguna
import com.rentech.server.db.PesanBantuan
import com.rentech.server.db.TiketBantuan
import com.rentech.server.jam
import com.rentech.server.sosial.PesanDto
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/** Disalin persis dari TopikBantuan di HubungiTimScreen.kt. */
enum class TopikBantuan(val label: String, val balasan: String) {
    Pembayaran(
        "Pembayaran dan dana",
        "Dana transfer ditahan Rentech sampai kamu mengonfirmasi barang diterima. Ceritakan pesanan mana yang bermasalah ya.",
    ),
    Verifikasi(
        "Verifikasi identitas",
        "Terima kasih, kami cek dulu ya. Kamu bisa kirim ulang foto KTP lewat tombol di bawah. Pastikan semua tulisan terbaca dan tidak ada pantulan cahaya.",
    ),
    BarangRusak(
        "Barang rusak atau tidak sesuai",
        "Maaf atas kendalanya. Kirim foto kondisi barang sekarang, nanti kami bandingkan dengan foto saat serah terima.",
    ),
    Deposit(
        "Pengembalian deposit",
        "Deposit kembali penuh setelah barang dikembalikan dalam kondisi baik. Sebutkan pesananmu supaya kami cek statusnya.",
    ),
    Lainnya("Lainnya", "Silakan ceritakan kendalamu, kami bantu sebisanya."),
}

@Serializable data class TiketDto(val id: Int, val topik: String, val status: String, val pesan: List<PesanDto>)

@Serializable data class TiketRingkasDto(val id: Int, val nama: String, val email: String, val topik: String, val pesanTerakhir: String, val waktu: String)

class BantuanLayanan {

    fun buka(penggunaId: Int, topik: String, teks: String?): TiketDto {
        val t = TopikBantuan.entries.firstOrNull { it.name == topik } ?: throw Gagal(HttpStatusCode.BadRequest, "Topik tidak dikenal")
        val id = transaction {
            val tiket = TiketBantuan.insertAndGetId { it[pengguna] = penggunaId; it[TiketBantuan.topik] = t.name }.value
            tambah(tiket, dariAdmin = false, teks?.trim()?.takeIf { it.isNotEmpty() } ?: t.label)
            tambah(tiket, dariAdmin = true, t.balasan)
            tiket
        }
        return TiketDto(id, t.name, "terbuka", pesan(id, penggunaId, setelah = 0))
    }

    /** Pesan tiket milik pengguna. Tiket orang lain dianggap tidak ada. */
    fun pesan(tiketId: Int, penggunaId: Int, setelah: Int): List<PesanDto> = transaction {
        val pemilik = TiketBantuan.select(TiketBantuan.pengguna).where { TiketBantuan.id eq tiketId }.firstOrNull()?.get(TiketBantuan.pengguna)?.value
        if (pemilik != penggunaId) throw Gagal(HttpStatusCode.NotFound, "Percakapan tidak ditemukan")
        isi(tiketId, setelah).map { it.copy(keluar = !it.keluar) }
    }

    fun kirim(tiketId: Int, penggunaId: Int, teks: String) {
        val bersih = teks.trim().ifEmpty { throw Gagal(HttpStatusCode.BadRequest, "Pesan masih kosong") }
        pesan(tiketId, penggunaId, Int.MAX_VALUE)
        transaction { tambah(tiketId, dariAdmin = false, bersih) }
    }

    /** Untuk dasbor admin. */
    fun balas(tiketId: Int, adminId: Int, teks: String) {
        pastikanAdmin(adminId)
        val bersih = teks.trim().ifEmpty { throw Gagal(HttpStatusCode.BadRequest, "Balasan masih kosong") }
        transaction {
            if (TiketBantuan.select(TiketBantuan.id).where { TiketBantuan.id eq tiketId }.empty()) throw Gagal(HttpStatusCode.NotFound, "Tiket tidak ditemukan")
            tambah(tiketId, dariAdmin = true, bersih)
        }
    }

    /** Untuk dasbor admin: pesan dari sudut pandang admin (keluar = dari admin). */
    fun isiUntukAdmin(tiketId: Int): List<PesanDto> = transaction { isi(tiketId, 0) }

    fun daftarTerbuka(): List<TiketRingkasDto> = transaction {
        TiketBantuan.join(Pengguna, JoinType.INNER, TiketBantuan.pengguna, Pengguna.id)
            .selectAll().where { TiketBantuan.status eq "terbuka" }
            .orderBy(TiketBantuan.id, SortOrder.DESC)
            .map { r ->
                val terakhir = PesanBantuan.selectAll().where { PesanBantuan.tiket eq r[TiketBantuan.id] }
                    .orderBy(PesanBantuan.id, SortOrder.DESC).first()
                TiketRingkasDto(
                    id = r[TiketBantuan.id].value,
                    nama = r[Pengguna.nama],
                    email = r[Pengguna.email],
                    topik = TopikBantuan.valueOf(r[TiketBantuan.topik]).label,
                    pesanTerakhir = terakhir[PesanBantuan.teks],
                    waktu = jam(terakhir[PesanBantuan.dibuat]),
                )
            }
    }

    private fun tambah(tiketId: Int, dariAdmin: Boolean, teks: String) {
        PesanBantuan.insert { it[tiket] = tiketId; it[PesanBantuan.dariAdmin] = dariAdmin; it[PesanBantuan.teks] = teks }
    }

    /** keluar = dikirim admin. */
    private fun isi(tiketId: Int, setelah: Int) =
        PesanBantuan.selectAll().where { (PesanBantuan.tiket eq tiketId) and (PesanBantuan.id greater setelah) }
            .orderBy(PesanBantuan.id, SortOrder.ASC)
            .map { PesanDto(it[PesanBantuan.id].value, it[PesanBantuan.teks], jam(it[PesanBantuan.dibuat]), keluar = it[PesanBantuan.dariAdmin]) }
}
