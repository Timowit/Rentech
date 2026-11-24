package com.rentech.app.ui.jelajah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAksi
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KolomIsian
import com.rentech.app.ui.komponen.KotakCentang
import com.rentech.app.ui.komponen.Segmen
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun DaftarTungguScreen() {
    var peran by rememberSaveable { mutableIntStateOf(0) }
    var nama by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var kota by rememberSaveable { mutableStateOf("") }
    var kabari by rememberSaveable { mutableStateOf(true) }
    var terkirim by rememberSaveable { mutableStateOf(false) }
    val lengkap = nama.isNotBlank() && email.contains('@') && kota.isNotBlank()

    Column(Modifier.fillMaxSize().background(Background).imePadding()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .statusBarsPadding()
                    .padding(horizontal = 21.dp, vertical = 34.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                Ikon(R.drawable.logo_simbol, OnPrimary, ukuran = 48.dp)
                Text(
                    "Akses awal",
                    style = Teks.Label,
                    color = OnPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(13.dp))
                        .background(OnPrimary.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )
                Text("Rentech segera hadir", style = Teks.Display, color = OnPrimary)
                Text(
                    "Daftar tunggu untuk akses awal. Kami kabari saat aplikasi siap dipakai.",
                    style = Teks.Isi,
                    color = OnPrimary,
                )
            }
            Column(
                Modifier.padding(horizontal = 21.dp, vertical = 34.dp),
                verticalArrangement = Arrangement.spacedBy(21.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Saya ingin", style = Teks.Label, color = TextPrimary)
                    Segmen(
                        pilihan = listOf("Menyewa", "Menyewakan"),
                        terpilih = peran,
                        onPilih = { peran = it },
                        ikon = listOf(R.drawable.ikon_search, R.drawable.ikon_storefront),
                    )
                }
                KolomIsian("Nama lengkap", nama, { nama = it }, "Masukkan nama lengkap")
                KolomIsian("Email", email, { email = it }, "nama@email.com", jenisKeyboard = KeyboardType.Email)
                KolomIsian("Kota", kota, { kota = it }, "Kota tempat kamu tinggal")
                Row(
                    Modifier.fillMaxWidth().clickable { kabari = !kabari },
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    KotakCentang(kabari)
                    Text(
                        "Saya ingin dikabari saat Rentech diluncurkan.",
                        style = Teks.Isi,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        BilahAksi {
            Tombol(
                if (terkirim) "Kamu sudah terdaftar" else "Gabung daftar tunggu",
                onClick = { terkirim = true },
                modifier = Modifier.fillMaxWidth(),
                aktif = lengkap && !terkirim,
            )
            Text("Kami tidak membagikan emailmu ke pihak lain.", style = Teks.Keterangan, color = TextTertiary)
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun DaftarTungguPreview() {
    RentechTheme { DaftarTungguScreen() }
}
