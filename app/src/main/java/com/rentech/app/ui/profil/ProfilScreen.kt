package com.rentech.app.ui.profil

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.Segmen
import com.rentech.app.ui.komponen.TabPemilik
import com.rentech.app.ui.komponen.TabPenyewa
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.onboarding.Peran
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.Error
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
fun ProfilScreen(
    peran: Peran,
    onGantiPeran: (Peran) -> Unit,
    onBantuan: () -> Unit,
    onKeluar: () -> Unit,
    onPilihTab: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Background)) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(PaddingValues(start = 21.dp, end = 21.dp, top = 34.dp, bottom = 21.dp)),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(13.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painterResource(R.drawable.avatar_1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(89.dp).clip(CircleShape),
                )
                Text("Nama kamu", style = Teks.Judul, color = TextPrimary)
                Row(
                    Modifier
                        .clip(RoundedCornerShape(13.dp))
                        .background(SuccessContainer)
                        .padding(start = 10.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Ikon(R.drawable.ikon_verified_fill, Success, ukuran = 16.dp)
                    Text("Identitas terverifikasi", style = Teks.Label, color = Success)
                }
            }

            Column(
                Modifier.fillMaxWidth().kartu().padding(21.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Text("Peran aktif", style = Teks.IsiTebal, color = TextPrimary)
                Segmen(
                    pilihan = listOf("Penyewa", "Pemilik"),
                    terpilih = peran.ordinal,
                    onPilih = { onGantiPeran(Peran.entries[it]) },
                )
                Text(
                    "Satu akun, dua peran. Pindah peran mengubah beranda dan menu bawah.",
                    style = Teks.Keterangan,
                    color = TextSecondary,
                )
            }

            Column(Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 5.dp)) {
                BarisMenu(R.drawable.ikon_history, "Riwayat sewa") { }
                Pemisah()
                BarisMenu(R.drawable.ikon_credit_card, "Metode pembayaran") { }
                Pemisah()
                BarisMenu(R.drawable.ikon_help, "Bantuan", onBantuan)
                Pemisah()
                BarisMenu(R.drawable.ikon_settings, "Pengaturan") { }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .clickable(onClick = onKeluar)
                    .padding(vertical = 13.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ikon(R.drawable.ikon_logout, Error, ukuran = 20.dp)
                Text("Keluar", style = Teks.IsiTebal, color = Error)
            }
        }
        NavigasiBawah(
            tab = if (peran == Peran.Pemilik) TabPemilik else TabPenyewa,
            terpilih = 3,
            onPilih = onPilihTab,
        )
    }
}

@Composable
private fun BarisMenu(@DrawableRes ikon: Int, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WadahIkon(ikon, Primary, PrimaryContainer, ukuran = 40.dp, ukuranIkon = 20.dp, radius = 13.dp)
        Text(label, style = Teks.IsiTebal, color = TextPrimary, modifier = Modifier.weight(1f))
        Ikon(R.drawable.ikon_chevron_right, TextTertiary, ukuran = 20.dp)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun ProfilPreview() {
    RentechTheme { ProfilScreen(Peran.Penyewa, {}, {}, {}, {}) }
}
