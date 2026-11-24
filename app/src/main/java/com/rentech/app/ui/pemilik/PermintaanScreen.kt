package com.rentech.app.ui.pemilik

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.Segmen
import com.rentech.app.ui.komponen.TabPemilik
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer

private val tabPermintaan = listOf("Baru", "Berjalan", "Selesai")

@Composable
fun PermintaanScreen(onPilihTab: (Int) -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val masuk = remember { mutableStateListOf(*contohPermintaan.toTypedArray()) }
    val berjalan = remember { mutableStateListOf<Permintaan>() }

    Column(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Text("Permintaan", style = Teks.Display, color = TextPrimary)
            Segmen(
                pilihan = tabPermintaan,
                terpilih = tab,
                onPilih = { tab = it },
                jumlah = listOf(masuk.size, berjalan.size.takeIf { it > 0 }, null),
            )
            when (tab) {
                0 -> if (masuk.isEmpty()) Kosong("Belum ada permintaan baru.") else masuk.forEach { p ->
                    KartuPermintaan(
                        p,
                        onTolak = { masuk.remove(p) },
                        onTerima = { masuk.remove(p); berjalan.add(p) },
                    )
                }
                1 -> if (berjalan.isEmpty()) Kosong("Belum ada sewa yang berjalan.") else berjalan.forEach { p ->
                    KartuPermintaan(p, onTolak = null, onTerima = null)
                }
                else -> Kosong("Sewa yang selesai akan muncul di sini.")
            }
        }
        NavigasiBawah(TabPemilik, terpilih = 2, onPilih = onPilihTab)
    }
}

@Composable
private fun KartuPermintaan(p: Permintaan, onTolak: (() -> Unit)?, onTerima: (() -> Unit)?) {
    Column(
        Modifier
            .fillMaxWidth()
            .kartu()
            .padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PenyewaTerverifikasi(p.avatarPenyewa, 32.dp, Modifier.weight(1f))
            when {
                onTerima == null -> Lencana("Berjalan", OnPrimaryContainer, PrimaryContainer)
                p.status == StatusPermintaan.Baru -> Lencana("Baru", OnPrimaryContainer, PrimaryContainer)
                else -> Lencana("Menunggu", Warning, WarningContainer)
            }
        }
        Pemisah()
        RingkasBarang(p.fotoBarang, p.namaBarang, p.tanggalLengkap)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Kamu terima", style = Teks.Isi, color = TextSecondary)
            Text(p.total, style = Teks.IsiTebal, color = TextPrimary)
        }
        if (onTolak != null && onTerima != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Tombol("Tolak", onClick = onTolak, gaya = GayaTombol.Garis, tinggi = 44.dp)
                Tombol("Terima", onClick = onTerima, tinggi = 44.dp)
            }
        }
    }
}

@Composable
private fun Kosong(teks: String) {
    Text(
        teks,
        style = Teks.Isi,
        color = TextTertiary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
    )
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun PermintaanPreview() {
    RentechTheme { PermintaanScreen({}) }
}
