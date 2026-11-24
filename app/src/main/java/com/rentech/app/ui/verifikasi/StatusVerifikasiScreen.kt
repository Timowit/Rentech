package com.rentech.app.ui.verifikasi

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Error
import com.rentech.app.ui.theme.ErrorContainer
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer

private val paddingStatus = PaddingValues(start = 21.dp, end = 21.dp, top = 89.dp, bottom = 21.dp)

@Composable
fun VerifikasiDiperiksaScreen(onJelajahi: () -> Unit) {
    KerangkaLayar(
        paddingIsi = paddingStatus,
        rataTengah = true,
        bawah = {
            Tombol("Jelajahi Rentech", onClick = onJelajahi, gaya = GayaTombol.Lembut, modifier = Modifier.fillMaxWidth())
        },
    ) {
        WadahIkon(
            R.drawable.ikon_schedule, Warning, WarningContainer,
            ukuran = 89.dp, ukuranIkon = 40.dp, radius = 28.dp,
        )
        JudulTengah(
            "Dokumen sedang diperiksa",
            "Biasanya selesai dalam 1×24 jam. Kami kabari lewat notifikasi dan email.",
        )
        Column(
            Modifier
                .fillMaxWidth()
                .kartu()
                .padding(horizontal = 21.dp, vertical = 8.dp),
        ) {
            BarisTahap(R.drawable.ikon_check_circle_fill, Success, "Akun dibuat", "Selesai")
            BarisTahap(R.drawable.ikon_check_circle_fill, Success, "KTP dan swafoto", "Terkirim")
            BarisTahap(R.drawable.ikon_schedule, Warning, "Pemeriksaan tim", "Sedang berjalan")
        }
        KotakInfo(
            R.drawable.ikon_info, TextSecondary, SurfaceVariant, TextSecondary,
            "Sambil menunggu, kamu sudah bisa menjelajah dan menyimpan barang. Menyewa dan menyewakan aktif setelah verifikasi.",
        )
    }
}

@Composable
fun VerifikasiDitolakScreen(
    alasan: List<String>,
    onKirimUlang: () -> Unit,
    onHubungiTim: () -> Unit,
) {
    KerangkaLayar(
        paddingIsi = paddingStatus,
        rataTengah = true,
        bawah = {
            Tombol("Kirim ulang dokumen", onClick = onKirimUlang, modifier = Modifier.fillMaxWidth())
            TautanTeks("Hubungi tim Rentech", onClick = onHubungiTim)
        },
    ) {
        WadahIkon(
            R.drawable.ikon_close, Error, ErrorContainer,
            ukuran = 89.dp, ukuranIkon = 40.dp, radius = 28.dp,
        )
        JudulTengah(
            "Verifikasi belum berhasil",
            "Kamu bisa mengirim ulang dokumen. Tidak ada biaya.",
        )
        Column(
            Modifier
                .fillMaxWidth()
                .kartu()
                .padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Text("Alasan", style = Teks.IsiTebal, color = TextPrimary)
            alasan.forEach { a ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Ikon(R.drawable.ikon_info, Error, ukuran = 18.dp)
                    Text(a, style = Teks.Isi, color = TextSecondary, modifier = Modifier.weight(1f))
                }
            }
        }
        KotakInfo(
            R.drawable.ikon_photo_camera, Primary, PrimaryContainer, OnPrimaryContainer,
            "Tips: foto di tempat terang, letakkan KTP di permukaan polos, dan pastikan tidak ada pantulan cahaya.",
        )
    }
}

@Composable
private fun JudulTengah(judul: String, isi: String) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(judul, style = Teks.Judul, color = TextPrimary, textAlign = TextAlign.Center)
        Text(isi, style = Teks.Isi, color = TextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
private fun BarisTahap(@DrawableRes ikon: Int, warna: Color, judul: String, status: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ikon(ikon, warna, ukuran = 22.dp)
        Text(judul, style = Teks.IsiTebal, color = TextPrimary, modifier = Modifier.weight(1f))
        Text(status, style = Teks.Keterangan, color = warna)
    }
}

@Composable
private fun KotakInfo(@DrawableRes ikon: Int, warnaIkon: Color, latar: Color, warnaTeks: Color, teks: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(latar)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Ikon(ikon, warnaIkon, ukuran = 20.dp)
        Text(teks, style = Teks.Keterangan, color = warnaTeks, modifier = Modifier.weight(1f))
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun DiperiksaPreview() {
    RentechTheme { VerifikasiDiperiksaScreen({}) }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun DitolakPreview() {
    RentechTheme {
        VerifikasiDitolakScreen(
            listOf("Foto KTP buram, nomor NIK tidak terbaca", "Wajah pada swafoto tertutup bayangan"),
            {}, {},
        )
    }
}
