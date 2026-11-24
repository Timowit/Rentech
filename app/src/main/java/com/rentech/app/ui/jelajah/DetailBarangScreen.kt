package com.rentech.app.ui.jelajah

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.contohBarang
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.BilahAksi
import com.rentech.app.ui.komponen.HargaPerHari
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.pemilik.Avatar
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.Error
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun DetailBarangScreen(
    barang: Barang,
    onKembali: () -> Unit,
    onPemilik: () -> Unit,
    onPerlindungan: () -> Unit,
    onSewa: () -> Unit,
) {
    var disimpan by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Background)) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(330.dp)) {
                Image(
                    painterResource(barang.foto),
                    contentDescription = barang.nama,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(21.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TombolBulat(R.drawable.ikon_arrow_back, onKembali, ukuranIkon = 22.dp, bergaris = false)
                    TombolBulat(
                        R.drawable.ikon_favorite,
                        onClick = { disimpan = !disimpan },
                        ukuranIkon = 22.dp,
                        bergaris = false,
                        warnaIkon = if (disimpan) Error else TextPrimary,
                    )
                }
                Text(
                    "1 / 1",
                    style = Teks.Label,
                    color = OnPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(21.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0x99121726))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
            Column(
                Modifier.padding(21.dp),
                verticalArrangement = Arrangement.spacedBy(21.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Lencana(barang.kategori, OnPrimaryContainer, PrimaryContainer)
                        Lencana("Tersedia", Success, SuccessContainer)
                    }
                    Text(barang.nama, style = Teks.Judul, color = TextPrimary)
                    Text(barang.kelengkapan, style = Teks.Isi, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ikon(R.drawable.ikon_location_on, TextTertiary, ukuran = 16.dp)
                        Text("${barang.lokasi} · ${barang.jarak}", style = Teks.Keterangan, color = TextTertiary)
                    }
                }

                Row(
                    Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Harga sewa", style = Teks.Keterangan, color = TextTertiary)
                        HargaPerHari(rupiah(barang.hargaPerHari), gaya = Teks.Judul, gayaSatuan = Teks.Isi)
                    }
                    Box(Modifier.width(1.dp).height(44.dp).background(Outline))
                    Column(Modifier.padding(start = 21.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Deposit", style = Teks.Keterangan, color = TextTertiary)
                        Text(rupiah(barang.deposit), style = Teks.IsiTebal, color = TextPrimary)
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .kartu()
                        .clickable(onClick = onPemilik)
                        .padding(horizontal = 21.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(R.drawable.avatar_2, 48.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Pemilik terverifikasi", style = Teks.IsiTebal, color = TextPrimary)
                            Ikon(R.drawable.ikon_verified_fill, Primary, ukuran = 16.dp)
                        }
                        Text(
                            "Identitas sudah diverifikasi sebelum bisa menyewakan",
                            style = Teks.Keterangan,
                            color = TextSecondary,
                        )
                    }
                    Ikon(R.drawable.ikon_chevron_right, TextTertiary, ukuran = 20.dp)
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .kartu()
                        .clickable(onClick = onPerlindungan)
                        .padding(21.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ikon(R.drawable.ikon_verified_user_fill, Primary, ukuran = 22.dp)
                        Text("Perlindungan Rentech", style = Teks.JudulBilah, color = TextPrimary)
                    }
                    BarisPerlindungan(R.drawable.ikon_lock, "Dana ditahan", "Pembayaranmu disimpan sampai barang kamu terima.")
                    BarisPerlindungan(
                        R.drawable.ikon_account_balance_wallet,
                        "Deposit dikembalikan",
                        "Deposit dikembalikan setelah barang kembali sesuai kondisi.",
                    )
                    BarisPerlindungan(
                        R.drawable.ikon_shield,
                        "Perlindungan kerusakan",
                        "Kerusakan selama sewa ditangani sesuai ketentuan yang tertera.",
                    )
                    BarisPerlindungan(
                        R.drawable.ikon_rate_review,
                        "Ulasan dua arah",
                        "Penyewa dan pemilik saling menilai setelah sewa selesai.",
                    )
                }
            }
        }
        BilahAksi {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Mulai dari", style = Teks.Keterangan, color = TextTertiary)
                    HargaPerHari(rupiah(barang.hargaPerHari), gaya = Teks.JudulBilah, warna = TextPrimary)
                }
                Tombol("Sewa sekarang", onClick = onSewa, modifier = Modifier.width(200.dp))
            }
        }
    }
}

@Composable
private fun BarisPerlindungan(@DrawableRes ikon: Int, judul: String, isi: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        WadahIkon(ikon, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 20.dp, radius = 13.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(isi, style = Teks.Keterangan, color = TextSecondary)
        }
    }
}

@Preview(heightDp = 1189, widthDp = 412)
@Composable
private fun DetailPreview() {
    RentechTheme { DetailBarangScreen(contohBarang.first(), {}, {}, {}, {}) }
}
