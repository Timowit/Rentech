package com.rentech.app.ui.pemilik

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.TabPemilik
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
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

private data class BarangSaya(
    val nama: String,
    @DrawableRes val foto: Int,
    val harga: String,
    val status: String,
    val disewa: Boolean,
)

private val barangSaya = listOf(
    BarangSaya("Kamera mirrorless full-frame", R.drawable.foto_kamera, "Rp350.000/hari", "Disewa sampai 15 Des", true),
    BarangSaya("Drone lipat 4K", R.drawable.foto_drone, "Rp200.000/hari", "Tersedia", false),
)

@Composable
fun BerandaPemilikScreen(
    onTambahBarang: () -> Unit,
    onLihatPermintaan: () -> Unit,
    onNotifikasi: () -> Unit,
    onPilihTab: (Int) -> Unit,
) {
    val permintaan = remember {
        mutableStateListOf(*contohPermintaan.filter { it.status == StatusPermintaan.Baru }.toTypedArray())
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painterResource(R.drawable.logo_rentech), contentDescription = "Rentech", Modifier.width(97.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Lencana("Mode pemilik", OnPrimaryContainer, PrimaryContainer)
                    Box(
                        Modifier.size(40.dp).kartu(radius = 20.dp).clickable(onClick = onNotifikasi),
                        contentAlignment = Alignment.Center,
                    ) {
                        Ikon(R.drawable.ikon_notifications, TextPrimary, ukuran = 20.dp)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ringkasan minggu ini", style = Teks.Isi, color = TextSecondary)
                Text("Sewakan barangmu", style = Teks.Display, color = TextPrimary)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                KotakAngka("${barangSaya.size}", "Barang aktif", Modifier.weight(1f))
                KotakAngka("${barangSaya.count { it.disewa }}", "Sewa berjalan", Modifier.weight(1f))
                KotakAngka(
                    "${permintaan.size}", "Permintaan baru", Modifier.weight(1f),
                    sorot = true, onClick = onLihatPermintaan,
                )
            }

            if (permintaan.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    JudulBagian("Perlu dibalas", "Lihat semua", onLihatPermintaan)
                    permintaan.forEach { p ->
                        KartuPerluDibalas(
                            p,
                            onTolak = { permintaan.remove(p) },
                            onTerima = { permintaan.remove(p) },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                JudulBagian("Barang kamu", "Kelola", onClick = {})
                barangSaya.forEach { KartuBarangSaya(it) }
                Tombol("Tambah barang", onClick = onTambahBarang, gaya = GayaTombol.Lembut, modifier = Modifier.fillMaxWidth())
            }
        }
        NavigasiBawah(TabPemilik, terpilih = 0, onPilih = onPilihTab)
    }
}

@Composable
private fun KotakAngka(
    angka: String,
    label: String,
    modifier: Modifier = Modifier,
    sorot: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val bentuk = RoundedCornerShape(18.dp)
    var m = if (sorot) modifier.clip(bentuk).background(Primary) else modifier.kartu(radius = 18.dp)
    if (onClick != null) m = m.clickable(onClick = onClick)
    Column(m.padding(13.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(angka, style = Teks.Judul, color = if (sorot) OnPrimary else TextPrimary)
        Text(label, style = Teks.Keterangan, color = if (sorot) OnPrimary else TextSecondary)
    }
}

@Composable
private fun JudulBagian(judul: String, aksi: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(judul, style = Teks.Judul, color = TextPrimary)
        TautanTeks(aksi, onClick = onClick)
    }
}

@Composable
private fun KartuPerluDibalas(p: Permintaan, onTolak: () -> Unit, onTerima: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .kartu()
            .padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        RingkasBarang(p.fotoBarang, p.namaBarang, p.tanggalSingkat)
        Row(verticalAlignment = Alignment.CenterVertically) {
            PenyewaTerverifikasi(p.avatarPenyewa, 28.dp, Modifier.weight(1f))
            Text(p.total, style = Teks.IsiTebal, color = TextPrimary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Tombol("Tolak", onClick = onTolak, gaya = GayaTombol.Garis, tinggi = 44.dp)
            Tombol("Terima", onClick = onTerima, tinggi = 44.dp)
        }
    }
}

@Composable
private fun KartuBarangSaya(b: BarangSaya) {
    Row(
        Modifier
            .fillMaxWidth()
            .kartu()
            .padding(start = 13.dp, top = 13.dp, bottom = 13.dp, end = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FotoBarang(b.foto, 66.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(b.nama, style = Teks.IsiTebal, color = TextPrimary)
            Text(b.harga, style = Teks.Keterangan, color = TextSecondary)
            if (b.disewa) Lencana(b.status, Warning, WarningContainer)
            else Lencana(b.status, Success, SuccessContainer)
        }
        Ikon(R.drawable.ikon_chevron_right, TextTertiary, ukuran = 20.dp)
    }
}

@Preview(heightDp = 1035, widthDp = 412)
@Composable
private fun BerandaPemilikPreview() {
    RentechTheme { BerandaPemilikScreen({}, {}, {}, {}) }
}

