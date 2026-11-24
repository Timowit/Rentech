package com.rentech.app.ui.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.chat.KolomPesan
import com.rentech.app.ui.chat.Pesan
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Topik bantuan beserta balasan pertama dari tim. Jawaban mengikuti alur di README. */
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

private sealed interface Isi {
    data class Gelembung(val pesan: Pesan) : Isi
    data object KirimUlangKtp : Isi
}

private val formatJam = DateTimeFormatter.ofPattern("HH.mm")
private fun sekarang() = LocalTime.now().format(formatJam)

private const val SAPAAN = "Halo! Ceritakan kendalamu. Pilih topik supaya kami bisa langsung membantu."
private const val TERIMA_PESAN = "Pesanmu sudah kami terima. Tim kami membalas di sini pada jam layanan."

/**
 * Percakapan dengan tim Rentech.
 * [topikAwal] dan [pesanAwal] diisi bila layar dibuka dari masalah tertentu,
 * misalnya verifikasi identitas yang ditolak.
 */
@Composable
fun HubungiTimScreen(
    onKembali: () -> Unit,
    onKirimUlangKtp: () -> Unit,
    topikAwal: TopikBantuan? = null,
    pesanAwal: String? = null,
) {
    var topik by rememberSaveable { mutableStateOf(topikAwal) }
    var draf by rememberSaveable { mutableStateOf("") }
    val isi = remember {
        mutableStateListOf<Isi>().apply {
            if (topikAwal != null) {
                val jam = sekarang()
                add(Isi.Gelembung(Pesan(pesanAwal ?: topikAwal.label, jam, keluar = true)))
                addAll(balasanUntuk(topikAwal, jam))
            }
        }
    }
    val daftar = rememberLazyListState()

    fun pilihTopik(t: TopikBantuan) {
        if (topik == t) return
        topik = t
        val jam = sekarang()
        isi.add(Isi.Gelembung(Pesan(t.label, jam, keluar = true)))
        isi.addAll(balasanUntuk(t, jam))
    }

    fun kirim() {
        val bersih = draf.trim()
        if (bersih.isEmpty()) return
        val jam = sekarang()
        isi.add(Isi.Gelembung(Pesan(bersih, jam, keluar = true)))
        isi.add(Isi.Gelembung(Pesan(TERIMA_PESAN, jam, keluar = false)))
        draf = ""
    }

    LaunchedEffect(isi.size) {
        if (isi.isNotEmpty()) daftar.animateScrollToItem(daftar.layoutInfo.totalItemsCount - 1)
    }

    Column(Modifier.fillMaxSize().background(Background).imePadding()) {
        BilahTim(onKembali)
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            state = daftar,
            contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            item { PenandaHari() }
            item { Gelembung(Pesan(SAPAAN, "09.02", keluar = false)) }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TopikBantuan.entries.forEach { t ->
                        Chip(
                            t.label,
                            terpilih = topik == t,
                            onClick = { pilihTopik(t) },
                            padding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        )
                    }
                }
            }
            items(isi) { item ->
                when (item) {
                    is Isi.Gelembung -> Gelembung(item.pesan)
                    Isi.KirimUlangKtp -> KartuKirimUlang(onKirimUlangKtp)
                }
            }
            item {
                KotakInfo(
                    R.drawable.ikon_lock,
                    "Tim Rentech tidak pernah meminta kata sandi atau kode OTP kamu.",
                    warna = TextSecondary,
                    latar = SurfaceVariant,
                    ukuranIkon = 16.dp,
                    radius = 13.dp,
                )
            }
        }
        KolomPesan(draf, onUbah = { draf = it }, onKirim = ::kirim, petunjuk = "Ceritakan kendalamu…")
    }
}

private fun balasanUntuk(t: TopikBantuan, jam: String): List<Isi> = buildList {
    add(Isi.Gelembung(Pesan(t.balasan, jam, keluar = false)))
    if (t == TopikBantuan.Verifikasi) add(Isi.KirimUlangKtp)
}

@Composable
private fun BilahTim(onKembali: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 21.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TombolBulat(R.drawable.ikon_arrow_back, onKembali, ukuranIkon = 22.dp)
        WadahIkon(R.drawable.ikon_support_agent, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 22.dp, radius = 12.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Tim Rentech", style = Teks.IsiTebal, color = TextPrimary)
            Text("Membalas pukul 08.00–22.00", style = Teks.Keterangan, color = TextSecondary)
        }
    }
}

@Composable
private fun PenandaHari() {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            "Hari ini",
            style = Teks.Label,
            color = TextSecondary,
            modifier = Modifier
                .clip(RoundedCornerShape(13.dp))
                .background(SurfaceVariant)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun Gelembung(p: Pesan) {
    val bentuk = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (p.keluar) 18.dp else 6.dp,
        bottomEnd = if (p.keluar) 6.dp else 18.dp,
    )
    Box(Modifier.fillMaxWidth(), contentAlignment = if (p.keluar) Alignment.CenterEnd else Alignment.CenterStart) {
        Column(
            Modifier
                .widthIn(max = 290.dp)
                .clip(bentuk)
                .background(if (p.keluar) Primary else Surface)
                .then(if (p.keluar) Modifier else Modifier.border(1.dp, Outline, bentuk))
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(p.teks, style = Teks.Isi, color = if (p.keluar) OnPrimary else TextPrimary)
            Text(p.waktu, style = Teks.Keterangan, color = if (p.keluar) OnPrimary.copy(alpha = 0.72f) else TextTertiary)
        }
    }
}

@Composable
private fun KartuKirimUlang(onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .kartu(radius = 18.dp, garis = Primary, tebalGaris = 1.5.dp)
            .clickable(onClick = onClick)
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WadahIkon(R.drawable.ikon_badge, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 22.dp, radius = 12.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Kirim ulang foto KTP", style = Teks.IsiTebal, color = TextPrimary)
            Text("Buka langkah verifikasi identitas", style = Teks.Keterangan, color = TextSecondary)
        }
        Ikon(R.drawable.ikon_chevron_right, Primary, ukuran = 20.dp)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun HubungiTimPreview() {
    RentechTheme {
        HubungiTimScreen(
            onKembali = {},
            onKirimUlangKtp = {},
            topikAwal = TopikBantuan.Verifikasi,
            pesanAwal = "Verifikasi identitas saya ditolak, katanya foto KTP buram.",
        )
    }
}
