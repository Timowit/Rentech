package com.rentech.app.ui.jelajah

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.contohBarang
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.TabPenyewa
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
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private data class Kategori(val nama: String, @DrawableRes val ikon: Int)

private val kategoriBeranda = listOf(
    Kategori("Kamera", R.drawable.ikon_photo_camera),
    Kategori("Laptop", R.drawable.ikon_laptop_mac),
    Kategori("Proyektor", R.drawable.ikon_projector),
    Kategori("Drone", R.drawable.ikon_drone),
    Kategori("Audio", R.drawable.ikon_headphones),
)

@Composable
fun BerandaScreen(
    onCari: () -> Unit,
    onFilter: () -> Unit,
    onCaraKerja: () -> Unit,
    onKategori: (String) -> Unit,
    onBarang: (Barang) -> Unit,
    onLihatSemua: () -> Unit,
    onPilihTab: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(vertical = 21.dp),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            val sisi = Modifier.padding(horizontal = 21.dp)
            BilahAtasBeranda(sisi)
            Pembuka(onCari, onFilter, sisi)
            HeroPerlindungan(onCaraKerja, sisi)
            Column(sisi, verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text("Kategori", style = Teks.Judul, color = TextPrimary)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    kategoriBeranda.forEach { k -> TombolKategori(k, onClick = { onKategori(k.nama) }) }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Row(sisi.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dekat kamu", style = Teks.Judul, color = TextPrimary, modifier = Modifier.alignByBaseline())
                    Text(
                        "Lihat semua",
                        style = Teks.IsiTebal,
                        color = Primary,
                        modifier = Modifier.alignByBaseline().clickable(onClick = onLihatSemua),
                    )
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 21.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    items(contohBarang.take(6), key = { it.id }) { b ->
                        KartuBarang(b, onClick = { onBarang(b) }, modifier = Modifier.width(178.dp))
                    }
                }
            }
            KenapaAman(sisi)
        }
        NavigasiBawah(TabPenyewa, terpilih = 0, onPilih = onPilihTab)
    }
}

@Composable
private fun BilahAtasBeranda(modifier: Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.logo_rentech), contentDescription = "Rentech")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .height(36.dp)
                    .kartu()
                    .clickable { }
                    .padding(start = 10.dp, end = 13.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ikon(R.drawable.ikon_location_on, Primary, ukuran = 16.dp)
                Text("Jakarta Selatan", style = Teks.Label, color = TextPrimary)
                Ikon(R.drawable.ikon_keyboard_arrow_down, TextTertiary, ukuran = 16.dp)
            }
            TombolBulat(R.drawable.ikon_notifications, onClick = { })
        }
    }
}

@Composable
private fun Pembuka(onCari: () -> Unit, onFilter: () -> Unit, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(21.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Sewa perangkat, tanpa perlu beli", style = Teks.IsiTebal, color = TextSecondary)
            Text("Mau sewa apa\nhari ini?", style = Teks.Display, color = TextPrimary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier
                    .weight(1f)
                    .height(55.dp)
                    .kartu(radius = 18.dp)
                    .clickable(onClick = onCari)
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ikon(R.drawable.ikon_search, TextTertiary, ukuran = 22.dp)
                Text("Cari kamera, laptop, drone…", style = Teks.Isi, color = TextTertiary)
            }
            Box(
                Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Primary)
                    .clickable(onClick = onFilter),
                contentAlignment = Alignment.Center,
            ) {
                Ikon(R.drawable.ikon_tune, OnPrimary, ukuran = 22.dp)
            }
        }
    }
}

@Composable
private fun HeroPerlindungan(onCaraKerja: () -> Unit, modifier: Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(229.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(Primary),
    ) {
        Column(
            Modifier
                .width(229.dp)
                .fillMaxHeight()
                .padding(start = 21.dp, end = 13.dp, top = 21.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(13.dp))
                        .background(OnPrimary.copy(alpha = 0.16f))
                        .padding(start = 8.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Ikon(R.drawable.ikon_verified_user_fill, OnPrimary, ukuran = 14.dp)
                    Text("Perlindungan Rentech", style = Teks.Label, color = OnPrimary)
                }
                Text("Dana ditahan sampai barang kamu terima", style = Teks.Judul, color = OnPrimary)
            }
            Row(
                Modifier
                    .clip(RoundedCornerShape(21.dp))
                    .background(Surface)
                    .clickable(onClick = onCaraKerja)
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Cara kerjanya", style = Teks.Label, color = Primary)
                Ikon(R.drawable.ikon_arrow_forward, Primary, ukuran = 16.dp)
            }
        }
        Image(
            painterResource(R.drawable.foto_kamera),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )
    }
}

@Composable
private fun TombolKategori(k: Kategori, onClick: () -> Unit) {
    val pertama = k.nama == "Kamera"
    Column(
        Modifier.width(66.dp).clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WadahIkon(
            k.ikon,
            warnaIkon = TextPrimary,
            latar = if (pertama) PrimaryContainer else Surface,
            modifier = Modifier.border(
                if (pertama) 1.5.dp else 1.dp,
                if (pertama) Primary else Outline,
                RoundedCornerShape(18.dp),
            ),
            ukuran = 55.dp,
            ukuranIkon = 24.dp,
            radius = 18.dp,
        )
        Text(k.nama, style = Teks.Label, color = TextSecondary)
    }
}

@Composable
private fun KenapaAman(modifier: Modifier) {
    Column(modifier.fillMaxWidth().kartu().padding(start = 21.dp, end = 21.dp, top = 21.dp, bottom = 8.dp)) {
        Text("Kenapa aman di Rentech", style = Teks.Judul, color = TextPrimary)
        Text("Perlindungan bawaan untuk penyewa dan pemilik.", style = Teks.Isi, color = TextSecondary)
        Spacer(Modifier.height(13.dp))
        AlasanAman(R.drawable.ikon_badge, "Identitas terverifikasi", "Setiap pengguna memverifikasi KTP sebelum bertransaksi.")
        AlasanAman(R.drawable.ikon_lock, "Deposit dikembalikan", "Deposit kembali penuh setelah barang diterima dalam kondisi baik.")
        AlasanAman(R.drawable.ikon_support_agent, "Dukungan manusia", "Tim kami siap membantu jika ada kendala saat sewa.")
    }
}

@Composable
fun AlasanAman(@DrawableRes ikon: Int, judul: String, isi: String, gayaIsi: TextStyle = Teks.Label) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        WadahIkon(ikon, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 20.dp, radius = 13.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(isi, style = gayaIsi, color = TextSecondary)
        }
    }
}

@Preview(heightDp = 1525, widthDp = 412)
@Composable
private fun BerandaPreview() {
    RentechTheme { BerandaScreen({}, {}, {}, {}, {}, {}, {}) }
}
