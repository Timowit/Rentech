package com.rentech.app.ui.sewa

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rentech.app.data.Barang
import com.rentech.app.data.cariBarang
import java.time.LocalDate
import java.time.temporal.ChronoUnit

const val BIAYA_LAYANAN = 25_000L
const val BIAYA_ANTAR = 25_000L

/** Hari yang sudah dipesan orang lain. Untuk sementara data contoh dari desain. */
val tanggalTidakTersedia = setOf(
    LocalDate.of(2026, 12, 20),
    LocalDate.of(2026, 12, 21),
    LocalDate.of(2026, 12, 22),
)

val slotWaktu = listOf("09.00–12.00", "13.00–16.00", "16.00–19.00")

/** Pilihan penyewa selama alur sewa, dari pilih tanggal sampai pembayaran. */
class DraftSewa(barangId: Int = 1) {
    var barangId by mutableIntStateOf(barangId)
    var mulai by mutableStateOf<LocalDate?>(LocalDate.of(2026, 12, 12))
    var selesai by mutableStateOf<LocalDate?>(LocalDate.of(2026, 12, 15))
    var diantar by mutableStateOf(false)
    var slot by mutableIntStateOf(0)
    var tunai by mutableStateOf(false)

    /** Penyewa sudah mengonfirmasi barang diterima di layar serah terima. */
    var diterima by mutableStateOf(false)

    val barang: Barang get() = cariBarang(barangId)
    val hari: Long get() = if (mulai != null && selesai != null) ChronoUnit.DAYS.between(mulai, selesai) else 0
    val biayaSewa: Long get() = hari * barang.hargaPerHari
    val biayaAntar: Long get() = if (diantar) BIAYA_ANTAR else 0
    val total: Long get() = biayaSewa + BIAYA_LAYANAN + biayaAntar + barang.deposit

    /** Ketukan pertama memilih tanggal mulai, ketukan kedua tanggal selesai. */
    fun pilihTanggal(t: LocalDate) {
        val m = mulai
        if (m == null || selesai != null || !t.isAfter(m) || adaYangTerpakai(m, t)) {
            mulai = t
            selesai = null
        } else {
            selesai = t
        }
    }

    private fun adaYangTerpakai(dari: LocalDate, sampai: LocalDate) =
        tanggalTidakTersedia.any { !it.isBefore(dari) && !it.isAfter(sampai) }
}

val singkatanHari = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
private val namaBulan = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
val namaBulanPanjang = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember",
)

fun hari(t: LocalDate) = singkatanHari[t.dayOfWeek.value - 1]
fun bulan(t: LocalDate) = namaBulan[t.monthValue - 1]

/** "Sab, 12 Des 2026" */
fun tanggalLengkap(t: LocalDate) = "${hari(t)}, ${t.dayOfMonth} ${bulan(t)} ${t.year}"

/** "Sab, 12 Des" */
fun tanggalPendek(t: LocalDate) = "${hari(t)}, ${t.dayOfMonth} ${bulan(t)}"

/** "Sab 12 – Sel 15 Des · 3 hari" */
fun ringkasRentang(d: DraftSewa): String {
    val m = d.mulai ?: return "Pilih tanggal mulai"
    val s = d.selesai ?: return "Mulai ${tanggalPendek(m)} · pilih tanggal selesai"
    val bulanMulai = if (m.month != s.month) " ${bulan(m)}" else ""
    return "${hari(m)} ${m.dayOfMonth}$bulanMulai – ${hari(s)} ${s.dayOfMonth} ${bulan(s)} · ${d.hari} hari"
}

/** "Sab, 12 – Sel, 15 Des 2026" */
fun rentangLengkap(d: DraftSewa): String {
    val m = d.mulai ?: return ""
    val s = d.selesai ?: return tanggalLengkap(m)
    val bulanMulai = if (m.month != s.month) " ${bulan(m)}" else ""
    return "${hari(m)}, ${m.dayOfMonth}$bulanMulai – ${hari(s)}, ${s.dayOfMonth} ${bulan(s)} ${s.year}"
}
