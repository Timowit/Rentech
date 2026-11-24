package com.rentech.app.ui.verifikasi

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.IndikatorLangkah
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun VerifikasiIdentitasScreen(
    onKembali: () -> Unit,
    onKirim: () -> Unit,
) {
    var ktpTerunggah by rememberSaveable { mutableStateOf(true) }
    var swafotoTerunggah by rememberSaveable { mutableStateOf(false) }
    val lengkap = ktpTerunggah && swafotoTerunggah

    KerangkaLayar(
        bilahAtas = { BilahAtas("Verifikasi identitas", onKembali) },
        paddingIsi = PaddingValues(start = 21.dp, end = 21.dp, top = 13.dp, bottom = 21.dp),
        bawah = {
            if (!lengkap) {
                Text(
                    if (!ktpTerunggah) "Menunggu foto KTP" else "Menunggu swafoto dengan KTP",
                    style = Teks.Keterangan,
                    color = TextTertiary,
                )
            }
            Tombol("Kirim untuk diperiksa", onClick = onKirim, aktif = lengkap, modifier = Modifier.fillMaxWidth())
        },
    ) {
        IndikatorLangkah(langkah = 2, total = 3)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Pastikan ini benar-benar kamu", style = Teks.Judul, color = TextPrimary)
            Text(
                "Kami memverifikasi identitas sebelum kamu bisa menyewa atau menyewakan barang.",
                style = Teks.Isi,
                color = TextSecondary,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            KartuDokumen(
                judul = "Foto KTP",
                keterangan = "Pastikan NIK dan foto terbaca jelas",
                namaBerkas = "ktp.jpg",
                ikon = R.drawable.ikon_badge,
                terunggah = ktpTerunggah,
                onUnggah = { ktpTerunggah = !ktpTerunggah },
            )
            KartuDokumen(
                judul = "Swafoto dengan KTP",
                keterangan = "Wajah dan KTP terlihat dalam satu foto",
                namaBerkas = "swafoto.jpg",
                ikon = R.drawable.ikon_photo_camera,
                terunggah = swafotoTerunggah,
                onUnggah = { swafotoTerunggah = !swafotoTerunggah },
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceVariant)
                .padding(horizontal = 18.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Ikon(R.drawable.ikon_lock, TextSecondary, ukuran = 20.dp)
            Text(
                "Dokumen dienkripsi dan hanya dipakai untuk verifikasi. Tidak ditampilkan ke pengguna lain.",
                style = Teks.Keterangan,
                color = TextSecondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun KartuDokumen(
    judul: String,
    keterangan: String,
    namaBerkas: String,
    @DrawableRes ikon: Int,
    terunggah: Boolean,
    onUnggah: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .kartu()
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (terunggah) {
            WadahIkon(R.drawable.ikon_check_circle_fill, Success, SuccessContainer)
        } else {
            WadahIkon(ikon, Primary, PrimaryContainer)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(
                if (terunggah) "Terunggah · $namaBerkas" else keterangan,
                style = Teks.Keterangan,
                color = if (terunggah) Success else TextSecondary,
            )
        }
        TombolKecil(
            teks = if (terunggah) "Ganti" else "Unggah",
            latar = if (terunggah) SurfaceVariant else Primary,
            warnaTeks = if (terunggah) TextPrimary else OnPrimary,
            onClick = onUnggah,
        )
    }
}

@Composable
private fun TombolKecil(teks: String, latar: Color, warnaTeks: Color, onClick: () -> Unit) {
    Text(
        teks,
        style = Teks.Label,
        color = warnaTeks,
        modifier = Modifier
            .clip(RoundedCornerShape(13.dp))
            .background(latar)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp),
    )
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun VerifikasiIdentitasPreview() {
    RentechTheme { VerifikasiIdentitasScreen({}, {}) }
}
