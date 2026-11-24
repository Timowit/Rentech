package com.rentech.app.ui.jelajah

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAksi
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private data class Pilar(@DrawableRes val ikon: Int, val judul: String, val isi: String)

private val pilarPerlindungan = listOf(
    Pilar(R.drawable.ikon_badge, "Verifikasi identitas", "Penyewa dan pemilik memverifikasi identitas sebelum bertransaksi."),
    Pilar(R.drawable.ikon_lock, "Dana ditahan", "Pembayaran disimpan sampai barang diterima oleh penyewa."),
    Pilar(R.drawable.ikon_account_balance_wallet, "Deposit", "Deposit dijaga selama sewa dan dikembalikan setelah barang kembali."),
    Pilar(R.drawable.ikon_shield, "Perlindungan kerusakan", "Kerusakan selama sewa ditangani sesuai ketentuan perlindungan."),
    Pilar(R.drawable.ikon_rate_review, "Ulasan dua arah", "Penyewa dan pemilik saling menilai setelah sewa selesai."),
    Pilar(R.drawable.ikon_support_agent, "Dukungan manusia", "Ada tim yang membantu saat terjadi masalah selama sewa."),
)

@Composable
fun PerlindunganScreen(onKembali: () -> Unit, onDaftarTunggu: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Background)) {
        BilahAtas("Perlindungan", onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                WadahIkon(R.drawable.ikon_verified_user_fill, OnPrimary, Primary, ukuran = 55.dp, ukuranIkon = 28.dp, radius = 18.dp)
                Text("Cara Rentech menjaga kamu", style = Teks.Display, color = TextPrimary)
                Text(
                    "Kepercayaan dibangun dari mekanisme yang jelas, bukan janji. Berikut yang berlaku di setiap sewa.",
                    style = Teks.Isi,
                    color = TextSecondary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                pilarPerlindungan.chunked(2).forEachIndexed { baris, pasangan ->
                    Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                        pasangan.forEachIndexed { i, p ->
                            KartuPilar(p, nomor = baris * 2 + i + 1, modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
        BilahAksi { Tombol("Gabung daftar tunggu", onClick = onDaftarTunggu, modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
private fun KartuPilar(p: Pilar, nomor: Int, modifier: Modifier) {
    Column(
        modifier.kartu().padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 21.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            WadahIkon(p.ikon, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 20.dp, radius = 13.dp)
            Text("%02d".format(nomor), style = Teks.Label, color = TextTertiary)
        }
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(p.judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(p.isi, style = Teks.Keterangan, color = TextSecondary)
        }
    }
}

@Preview(heightDp = 981, widthDp = 412)
@Composable
private fun PerlindunganPreview() {
    RentechTheme { PerlindunganScreen({}, {}) }
}
