package com.rentech.app.ui.notifikasi

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer

/** Layar yang dibuka saat notifikasi diketuk. */
enum class TujuanNotifikasi { SewaSaya, Chat, Ulasan, Tidak }

private enum class Jenis(@DrawableRes val ikon: Int, val warna: Color, val latar: Color) {
    Diterima(R.drawable.ikon_check_circle_fill, Success, SuccessContainer),
    Dana(R.drawable.ikon_account_balance_wallet, Primary, PrimaryContainer),
    Pesan(R.drawable.ikon_chat, Primary, PrimaryContainer),
    Pengingat(R.drawable.ikon_schedule, Warning, WarningContainer),
    Ulasan(R.drawable.ikon_star_fill, Warning, WarningContainer),
    Terverifikasi(R.drawable.ikon_verified_user_fill, Success, SuccessContainer),
}

private data class Notifikasi(
    val jenis: Jenis,
    val judul: String,
    val isi: String,
    val waktu: String,
    val hariIni: Boolean,
    val tujuan: TujuanNotifikasi,
)

// Contoh data, sama dengan layar G01 di Figma.
private val contohNotifikasi = listOf(
    Notifikasi(
        Jenis.Diterima, "Pemilik menerima pesananmu",
        "Kamera mirrorless full-frame · Sab, 12 – Sel, 15 Des 2026", "10 menit lalu",
        hariIni = true, TujuanNotifikasi.SewaSaya,
    ),
    Notifikasi(
        Jenis.Dana, "Bukti transfer sudah diperiksa",
        "Rp3.075.000 ditahan Rentech sampai barang kamu terima.", "1 jam lalu",
        hariIni = true, TujuanNotifikasi.SewaSaya,
    ),
    Notifikasi(
        Jenis.Pesan, "Pesan baru dari pemilik",
        "“Kameranya sudah saya cas penuh, ambil jam 10 ya.”", "2 jam lalu",
        hariIni = true, TujuanNotifikasi.Chat,
    ),
    Notifikasi(
        Jenis.Pengingat, "Waktunya mengembalikan barang",
        "Proyektor portabel dikembalikan hari ini sebelum 19.00.", "Min, 22 Nov · 08.00",
        hariIni = false, TujuanNotifikasi.SewaSaya,
    ),
    Notifikasi(
        Jenis.Ulasan, "Beri ulasan untuk sewamu",
        "Ulasan membantu penyewa lain memilih dengan lebih yakin.", "Min, 22 Nov · 20.00",
        hariIni = false, TujuanNotifikasi.Ulasan,
    ),
    Notifikasi(
        Jenis.Terverifikasi, "Identitas kamu terverifikasi",
        "Sekarang kamu bisa menyewa dan menyewakan barang.", "Sen, 16 Nov",
        hariIni = false, TujuanNotifikasi.Tidak,
    ),
)

/** Indeks notifikasi yang belum dibaca saat layar pertama dibuka. */
private val belumDibacaAwal = listOf(0, 1)

@Composable
fun NotifikasiScreen(onKembali: () -> Unit, onBuka: (TujuanNotifikasi) -> Unit) {
    val belumDibaca = rememberSaveable(
        saver = listSaver(save = { it.toList() }, restore = { mutableStateListOf(*it.toTypedArray()) }),
    ) { mutableStateListOf(*belumDibacaAwal.toTypedArray()) }

    Column(Modifier.fillMaxSize().background(Background)) {
        BilahAtas("Notifikasi", onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            listOf(true, false).forEach { hariIni ->
                val isi = contohNotifikasi.withIndex().filter { it.value.hariIni == hariIni }
                if (isi.isEmpty()) return@forEach
                Row(
                    Modifier.fillMaxWidth().padding(top = if (hariIni) 0.dp else 21.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(if (hariIni) "Hari ini" else "Sebelumnya", style = Teks.IsiTebal, color = TextPrimary)
                    if (hariIni && belumDibaca.isNotEmpty()) {
                        Text(
                            "Tandai semua dibaca",
                            style = Teks.IsiTebal,
                            color = Primary,
                            modifier = Modifier.clickable { belumDibaca.clear() },
                        )
                    }
                }
                Column(Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp)) {
                    isi.forEachIndexed { i, (indeks, n) ->
                        if (i > 0) Pemisah()
                        BarisNotifikasi(n, baru = indeks in belumDibaca) {
                            belumDibaca.remove(indeks)
                            onBuka(n.tujuan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarisNotifikasi(n: Notifikasi, baru: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 21.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        WadahIkon(n.jenis.ikon, n.jenis.warna, n.jenis.latar, ukuran = 40.dp, ukuranIkon = 20.dp, radius = 12.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(n.judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(n.isi, style = Teks.Label.copy(fontWeight = Teks.Isi.fontWeight), color = TextSecondary)
            Text(n.waktu, style = Teks.Keterangan, color = TextTertiary)
        }
        Box(Modifier.size(8.dp), contentAlignment = Alignment.Center) {
            if (baru) Box(Modifier.size(8.dp).clip(CircleShape).background(Primary))
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun NotifikasiPreview() {
    RentechTheme { NotifikasiScreen({}, {}) }
}
