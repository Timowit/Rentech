package com.rentech.server.pesanan

import com.rentech.server.Gagal
import com.rentech.server.akun.pastikanAdmin
import com.rentech.server.barang.BarangDto
import com.rentech.server.barang.PenggunaRingkasDto
import com.rentech.server.barang.barangAtau404
import com.rentech.server.barang.pastikanTerverifikasi
import com.rentech.server.barang.ringkas
import com.rentech.server.db.Barang
import com.rentech.server.db.Pengguna
import com.rentech.server.db.Pesanan
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.notifikasi.NotifikasiLayanan
import com.rentech.server.rupiah
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.sql.Connection
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

// Sama dengan DraftSewa.kt di aplikasi.
const val BIAYA_LAYANAN = 25_000L
const val BIAYA_ANTAR = 25_000L

/** Pesanan dengan status ini memakai tanggal barang. */
val STATUS_MEMBLOKIR = listOf("menunggu_pemilik", "diterima_pemilik", "berjalan")

@Serializable
data class PesananMasuk(
    val barangId: Int,
    val mulai: String,
    val selesai: String,
    val diantar: Boolean = false,
    val slot: Int = 0,
    val metode: String,
)

@Serializable
data class PesananDto(
    val id: Int,
    val barang: BarangDto,
    val penyewa: PenggunaRingkasDto,
    val mulai: String,
    val selesai: String,
    val hari: Long,
    val diantar: Boolean,
    val slot: Int,
    val metode: String,
    val kodeUnik: Int?,
    val biayaSewa: Long,
    val biayaLayanan: Long,
    val biayaAntar: Long,
    val deposit: Long,
    val total: Long,
    val status: String,
    val statusDana: String,
    val alasanBukti: String?,
)

class PesananLayanan(private val penyimpanan: Penyimpanan, private val notifikasi: NotifikasiLayanan) {

    fun buat(penyewaId: Int, m: PesananMasuk): PesananDto {
        pastikanTerverifikasi(penyewaId)
        val mulai = tanggal(m.mulai)
        val selesai = tanggal(m.selesai)
        if (!selesai.isAfter(mulai)) throw Gagal(HttpStatusCode.BadRequest, "Tanggal selesai harus setelah tanggal mulai")
        if (m.metode !in setOf("transfer", "tunai")) throw Gagal(HttpStatusCode.BadRequest, "Metode pembayaran tidak dikenal")
        if (m.slot !in 0..2) throw Gagal(HttpStatusCode.BadRequest, "Slot waktu tidak dikenal")
        val barang = barangAtau404(m.barangId)
        if (barang.pemilik.id == penyewaId) throw Gagal(HttpStatusCode.BadRequest, "Kamu tidak bisa menyewa barangmu sendiri")

        val hari = ChronoUnit.DAYS.between(mulai, selesai)
        val biayaSewa = hari * barang.hargaPerHari
        val biayaAntar = if (m.diantar) BIAYA_ANTAR else 0
        val kodeUnik = if (m.metode == "transfer") (100..999).random() else null
        val total = biayaSewa + BIAYA_LAYANAN + biayaAntar + barang.deposit + (kodeUnik ?: 0)

        // Serializable: dua pesanan bersamaan untuk tanggal yang sama tidak bisa sama-sama lolos.
        val id = transaction(transactionIsolation = Connection.TRANSACTION_SERIALIZABLE) {
            val bentrok = Pesanan.select(Pesanan.id).where {
                (Pesanan.barang eq m.barangId) and (Pesanan.status inList STATUS_MEMBLOKIR) and
                    (Pesanan.mulai lessEq selesai) and (Pesanan.selesai greaterEq mulai)
            }.any()
            if (bentrok) throw Gagal(HttpStatusCode.Conflict, "Tanggal itu sudah dipesan orang lain")
            Pesanan.insertAndGetId {
                it[Pesanan.barang] = m.barangId
                it[penyewa] = penyewaId
                it[Pesanan.mulai] = mulai
                it[Pesanan.selesai] = selesai
                it[diantar] = m.diantar
                it[slot] = m.slot
                it[metode] = m.metode
                it[Pesanan.kodeUnik] = kodeUnik?.toShort()
                it[Pesanan.biayaSewa] = biayaSewa
                it[biayaLayanan] = BIAYA_LAYANAN
                it[Pesanan.biayaAntar] = biayaAntar
                it[deposit] = barang.deposit
                it[Pesanan.total] = total
                it[statusDana] = if (m.metode == "tunai") "tunai" else "belum"
            }.value
        }
        return satu(id)
    }

    fun milikPenyewa(penyewaId: Int) = daftar(Pesanan.penyewa eq penyewaId)

    fun masukUntukPemilik(pemilikId: Int) = daftar(Barang.pemilik eq pemilikId)

    fun terima(pesananId: Int, pemilikId: Int) = putuskanPemilik(pesananId, pemilikId, "diterima_pemilik")

    fun tolak(pesananId: Int, pemilikId: Int) = putuskanPemilik(pesananId, pemilikId, "ditolak_pemilik")

    fun kirimBukti(pesananId: Int, penyewaId: Int, bukti: ByteArray): PesananDto {
        val p = baris(pesananId)
        if (p[Pesanan.penyewa].value != penyewaId) throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")
        if (p[Pesanan.metode] != "transfer") throw Gagal(HttpStatusCode.BadRequest, "Pesanan ini dibayar tunai")
        if (p[Pesanan.statusDana] != "belum") throw Gagal(HttpStatusCode.Conflict, "Bukti transfer sudah dikirim")
        if (p[Pesanan.status] !in STATUS_MEMBLOKIR) throw Gagal(HttpStatusCode.Conflict, "Pesanan ini sudah tidak aktif")
        val path = penyimpanan.simpan(bukti, privat = true)
        transaction {
            Pesanan.update({ Pesanan.id eq pesananId }) {
                it[buktiTransfer] = path
                it[statusDana] = "menunggu_cek"
                it[alasanBukti] = null
            }
        }
        return satu(pesananId)
    }

    /** Dipakai dasbor admin. Sesuai: dana ditahan. Tidak sesuai: penyewa diminta mengirim ulang. */
    fun periksaBukti(pesananId: Int, adminId: Int, sesuai: Boolean, alasan: String?) {
        pastikanAdmin(adminId)
        transaction {
            val p = baris(pesananId)
            if (p[Pesanan.statusDana] != "menunggu_cek") throw Gagal(HttpStatusCode.Conflict, "Bukti transfer tidak sedang menunggu pemeriksaan")
            val penyewaId = p[Pesanan.penyewa].value
            if (sesuai) {
                Pesanan.update({ Pesanan.id eq pesananId }) { it[statusDana] = "ditahan"; it[alasanBukti] = null }
                notifikasi.kirim(
                    penyewaId, "dana", "Bukti transfer sudah diperiksa",
                    "${rupiah(p[Pesanan.total])} ditahan Rentech sampai barang kamu terima.", "sewa_saya",
                )
            } else {
                val teks = alasan?.trim()?.takeIf { it.isNotEmpty() } ?: throw Gagal(HttpStatusCode.BadRequest, "Tulis alasan bukti tidak sesuai")
                Pesanan.update({ Pesanan.id eq pesananId }) { it[statusDana] = "belum"; it[alasanBukti] = teks }
                notifikasi.kirim(penyewaId, "dana", "Bukti transfer perlu dikirim ulang", teks, "sewa_saya")
            }
        }
    }

    /** Penyewa mengonfirmasi barang sudah diterima: sewa berjalan, dana yang ditahan diteruskan ke pemilik. */
    fun konfirmasiDiterima(pesananId: Int, penyewaId: Int): PesananDto {
        transaction {
            val p = baris(pesananId)
            if (p[Pesanan.penyewa].value != penyewaId) throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")
            if (p[Pesanan.status] != "diterima_pemilik") throw Gagal(HttpStatusCode.Conflict, "Pesanan belum diterima pemilik")
            Pesanan.update({ Pesanan.id eq pesananId }) {
                it[status] = "berjalan"
                it[diterimaPenyewaPada] = OffsetDateTime.now()
                if (p[Pesanan.statusDana] == "ditahan") it[statusDana] = "diteruskan"
            }
        }
        return satu(pesananId)
    }

    fun satu(id: Int): PesananDto = daftar(Pesanan.id eq id).firstOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")

    private fun putuskanPemilik(pesananId: Int, pemilikId: Int, hasil: String): PesananDto {
        transaction {
            val p = baris(pesananId)
            val pemilik = Barang.select(Barang.pemilik).where { Barang.id eq p[Pesanan.barang] }.single()[Barang.pemilik].value
            if (pemilik != pemilikId) throw Gagal(HttpStatusCode.Forbidden, "Hanya pemilik barang yang bisa memutuskan")
            if (p[Pesanan.status] != "menunggu_pemilik") throw Gagal(HttpStatusCode.Conflict, "Pesanan ini sudah diputuskan")
            Pesanan.update({ Pesanan.id eq pesananId }) { it[status] = hasil }
            val namaBarang = Barang.select(Barang.nama).where { Barang.id eq p[Pesanan.barang] }.single()[Barang.nama]
            if (hasil == "diterima_pemilik") {
                notifikasi.kirim(p[Pesanan.penyewa].value, "diterima", "Pemilik menerima pesananmu", "$namaBarang · ${rentang(p)}", "sewa_saya")
            } else {
                notifikasi.kirim(p[Pesanan.penyewa].value, "pesan", "Pemilik belum bisa menerima pesananmu", "$namaBarang · ${rentang(p)}", "sewa_saya")
            }
        }
        return satu(pesananId)
    }


    private fun baris(id: Int): ResultRow = transaction { Pesanan.selectAll().where { Pesanan.id eq id }.firstOrNull() }
        ?: throw Gagal(HttpStatusCode.NotFound, "Pesanan tidak ditemukan")

    private fun daftar(filter: Op<Boolean>): List<PesananDto> = transaction {
        Pesanan
            .join(Barang, JoinType.INNER, Pesanan.barang, Barang.id)
            .join(Pengguna, JoinType.INNER, Pesanan.penyewa, Pengguna.id)
            .selectAll().where { filter }
            .orderBy(Pesanan.id, SortOrder.DESC)
            .map { r ->
                PesananDto(
                    id = r[Pesanan.id].value,
                    barang = barangAtau404(r[Pesanan.barang].value),
                    penyewa = ringkas(r),
                    mulai = r[Pesanan.mulai].toString(),
                    selesai = r[Pesanan.selesai].toString(),
                    hari = ChronoUnit.DAYS.between(r[Pesanan.mulai], r[Pesanan.selesai]),
                    diantar = r[Pesanan.diantar],
                    slot = r[Pesanan.slot],
                    metode = r[Pesanan.metode],
                    kodeUnik = r[Pesanan.kodeUnik]?.toInt(),
                    biayaSewa = r[Pesanan.biayaSewa],
                    biayaLayanan = r[Pesanan.biayaLayanan],
                    biayaAntar = r[Pesanan.biayaAntar],
                    deposit = r[Pesanan.deposit],
                    total = r[Pesanan.total],
                    status = r[Pesanan.status],
                    statusDana = r[Pesanan.statusDana],
                    alasanBukti = r[Pesanan.alasanBukti],
                )
            }
    }
}

private fun tanggal(s: String): LocalDate =
    runCatching { LocalDate.parse(s) }.getOrElse { throw Gagal(HttpStatusCode.BadRequest, "Format tanggal harus YYYY-MM-DD") }

private val HARI = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
private val BULAN = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

/** "Sab, 12 – Sel, 15 Des 2026", sama dengan rentangLengkap() di aplikasi. */
fun rentang(mulai: LocalDate, selesai: LocalDate): String {
    fun h(t: LocalDate) = HARI[t.dayOfWeek.value - 1]
    val bulanMulai = if (mulai.month != selesai.month) " ${BULAN[mulai.monthValue - 1]}" else ""
    return "${h(mulai)}, ${mulai.dayOfMonth}$bulanMulai – ${h(selesai)}, ${selesai.dayOfMonth} ${BULAN[selesai.monthValue - 1]} ${selesai.year}"
}

private fun rentang(p: ResultRow) = rentang(p[Pesanan.mulai], p[Pesanan.selesai])
