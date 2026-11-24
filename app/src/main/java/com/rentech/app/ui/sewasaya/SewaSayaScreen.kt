package com.rentech.app.ui.sewasaya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.contohBarang
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.Segmen
import com.rentech.app.ui.komponen.TabPenyewa
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.pemilik.FotoBarang
import com.rentech.app.ui.sewa.DraftSewa
import com.rentech.app.ui.sewa.rentangLengkap
import com.rentech.app.ui.sewa.tanggalPendek
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer

/** Sewa yang sudah selesai, contoh untuk tab Riwayat. */
data class SewaSelesai(val barang: Barang, val tanggal: String)

val contohRiwayat = listOf(SewaSelesai(contohBarang[2], "Sab, 21 – Min, 22 Nov 2026"))

private enum class Langkah { Selesai, Sekarang, Nanti }

private data class Tahap(val judul: String, val keterangan: String, val langkah: Langkah)

@Composable
fun SewaSayaScreen(
    draft: DraftSewa,
    onKonfirmasi: () -> Unit,
    onChat: () -> Unit,
    onUlas: (SewaSelesai) -> Unit,
    onPilihTab: (Int) -> Unit,
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Text("Sewa saya", style = Teks.Display, color = TextPrimary)
            Segmen(listOf("Aktif", "Riwayat"), terpilih = tab, onPilih = { tab = it })
            if (tab == 0) KartuSewaAktif(draft, onKonfirmasi, onChat) else contohRiwayat.forEach { KartuRiwayat(it, onUlas) }
        }
        NavigasiBawah(TabPenyewa, terpilih = 2, onPilih = onPilihTab)
    }
}

@Composable
private fun KartuSewaAktif(draft: DraftSewa, onKonfirmasi: () -> Unit, onChat: () -> Unit) {
    val m = draft.mulai
    val tahap = listOf(
        Tahap(
            if (draft.tunai) "Pesanan diterima pemilik" else "Pembayaran diterima, dana ditahan",
            m?.let { "${tanggalPendek(it)} · 09.12" }.orEmpty(),
            Langkah.Selesai,
        ),
        Tahap("Barang diserahkan pemilik", m?.let { "${tanggalPendek(it)} · 10.30" }.orEmpty(), Langkah.Selesai),
        Tahap(
            "Konfirmasi barang diterima",
            if (draft.diterima) "Sudah kamu konfirmasi" else "Menunggu kamu",
            if (draft.diterima) Langkah.Selesai else Langkah.Sekarang,
        ),
        Tahap("Barang dikembalikan", draft.selesai?.let { tanggalPendek(it) }.orEmpty(), if (draft.diterima) Langkah.Sekarang else Langkah.Nanti),
        Tahap("Deposit dikembalikan", "Setelah barang dicek pemilik", Langkah.Nanti),
    )
    Column(
        Modifier.fillMaxWidth().kartu().padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(21.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
            FotoBarang(draft.barang.foto, 66.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(draft.barang.nama, style = Teks.IsiTebal, color = TextPrimary)
                Text(rentangLengkap(draft), style = Teks.Keterangan, color = TextSecondary)
                if (draft.tunai) Lencana("Bayar tunai", Warning, WarningContainer)
                else Lencana("Dana ditahan", OnPrimaryContainer, PrimaryContainer)
            }
        }
        Pemisah()
        Column {
            tahap.forEachIndexed { i, t -> BarisTahap(t, terakhir = i == tahap.lastIndex) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            TombolBulat(R.drawable.ikon_chat, onChat, ukuran = 55.dp, ukuranIkon = 22.dp, bentukBulat = false)
            Tombol(
                if (draft.diterima) "Barang sudah diterima" else "Konfirmasi diterima",
                onClick = onKonfirmasi,
                modifier = Modifier.weight(1f),
                aktif = !draft.diterima,
            )
        }
    }
}

@Composable
private fun BarisTahap(t: Tahap, terakhir: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        Column(Modifier.width(24.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (t.langkah) {
                            Langkah.Selesai -> Success
                            Langkah.Sekarang -> Primary
                            Langkah.Nanti -> SurfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when (t.langkah) {
                    Langkah.Selesai -> Ikon(R.drawable.ikon_check, OnPrimary, ukuran = 16.dp)
                    Langkah.Sekarang -> Box(Modifier.size(8.dp).clip(CircleShape).background(OnPrimary))
                    Langkah.Nanti -> {}
                }
            }
            if (!terakhir) {
                Box(
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (t.langkah == Langkah.Selesai) Success else Outline)
                )
            }
        }
        Column(Modifier.weight(1f).padding(bottom = 13.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            val nanti = t.langkah == Langkah.Nanti
            Text(t.judul, style = if (nanti) Teks.Isi else Teks.IsiTebal, color = if (nanti) TextTertiary else TextPrimary)
            Text(
                t.keterangan,
                style = Teks.Keterangan,
                color = if (t.langkah == Langkah.Sekarang) Primary else TextTertiary,
            )
        }
    }
}

@Composable
private fun KartuRiwayat(s: SewaSelesai, onUlas: (SewaSelesai) -> Unit) {
    Column(
        Modifier.fillMaxWidth().kartu().padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
            FotoBarang(s.barang.foto, 66.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(s.barang.nama, style = Teks.IsiTebal, color = TextPrimary)
                Text(s.tanggal, style = Teks.Keterangan, color = TextSecondary)
                Lencana("Sewa selesai", Success, SuccessContainer)
            }
        }
        Tombol("Beri ulasan", onClick = { onUlas(s) }, modifier = Modifier.fillMaxWidth(), gaya = GayaTombol.Lembut, tinggi = 44.dp)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun SewaSayaPreview() {
    RentechTheme { SewaSayaScreen(DraftSewa(), {}, {}, {}, {}) }
}
