package com.rentech.app.ui.jelajah

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.cariBarang
import com.rentech.app.data.contohBarang
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.pemilik.Avatar
import com.rentech.app.ui.sewa.KerangkaSewa
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning

/** Barang milik pemilik ini: barang yang sedang dilihat ditambah satu barang lain sebagai contoh. */
private fun barangPemilik(barang: Barang): List<Barang> =
    listOf(barang, cariBarang(if (barang.id == 6) 1 else 6))

@Composable
fun ProfilPemilikScreen(
    barang: Barang,
    onKembali: () -> Unit,
    onBarang: (Barang) -> Unit,
    onKirimPesan: () -> Unit,
) {
    KerangkaSewa(
        judul = "Profil pemilik",
        onKembali = onKembali,
        aksi = {
            Tombol("Kirim pesan ke pemilik", onClick = onKirimPesan, modifier = Modifier.fillMaxWidth(), gaya = GayaTombol.Garis)
        },
    ) {
        Column(
            Modifier.fillMaxWidth().kartu().padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Avatar(R.drawable.avatar_2, 89.dp)
            Text("Pemilik terverifikasi", style = Teks.Judul, color = TextPrimary)
            Lencana("Identitas terverifikasi", Success, SuccessContainer)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Ikon(R.drawable.ikon_location_on, TextTertiary, ukuran = 16.dp)
                Text("Kemang, Jakarta Selatan · bergabung Nov 2026", style = Teks.Keterangan, color = TextTertiary)
            }
        }

        Column(Modifier.fillMaxWidth().kartu().padding(start = 21.dp, end = 21.dp, top = 18.dp, bottom = 8.dp)) {
            Text("Yang sudah diverifikasi", style = Teks.IsiTebal, color = TextPrimary)
            BarisVerifikasi(R.drawable.ikon_badge, "KTP dan swafoto")
            BarisVerifikasi(R.drawable.ikon_mail, "Email")
            BarisVerifikasi(R.drawable.ikon_smartphone, "Nomor HP")
        }

        Text("Barang yang disewakan", style = Teks.Judul, color = TextPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            barangPemilik(barang).forEach { b ->
                KartuBarang(b, onClick = { onBarang(b) }, modifier = Modifier.weight(1f))
            }
        }

        Text("Ulasan dari penyewa", style = Teks.Judul, color = TextPrimary)
        Column(
            Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Avatar(R.drawable.avatar_3, 32.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Penyewa terverifikasi", style = Teks.IsiTebal, color = TextPrimary)
                    Text("Sewa kamera · Nov 2026", style = Teks.Keterangan, color = TextTertiary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(5) { Ikon(R.drawable.ikon_star_fill, Warning, ukuran = 16.dp) }
                }
            }
            Text("Kamera bersih dan sesuai foto. Serah terima di Kemang tepat waktu.", style = Teks.Isi, color = TextSecondary)
        }
    }
}

@Composable
private fun BarisVerifikasi(@DrawableRes ikon: Int, label: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ikon(ikon, TextSecondary, ukuran = 20.dp)
        Text(label, style = Teks.Isi, color = TextPrimary, modifier = Modifier.weight(1f))
        Ikon(R.drawable.ikon_check_circle_fill, Success, ukuran = 20.dp)
    }
}

@Preview(heightDp = 1097, widthDp = 412)
@Composable
private fun ProfilPemilikPreview() {
    RentechTheme { ProfilPemilikScreen(contohBarang.first(), {}, {}, {}) }
}
