package com.rentech.app.ui.sewa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

@Composable
fun MenungguKonfirmasiScreen(draft: DraftSewa, onLihatPesanan: () -> Unit, onBeranda: () -> Unit) {
    val judul = if (draft.tunai) "Pesanan terkirim" else "Pembayaran diterima"
    val isi = if (draft.tunai) {
        "Sekarang menunggu pemilik menerima pesananmu. Bayar tunai ke pemilik saat serah terima setelah barang dicek."
    } else {
        "Sekarang menunggu pemilik menerima pesananmu. Pemilik punya waktu 12 jam untuk membalas."
    }
    val jaminan = if (draft.tunai) {
        "Jika pemilik menolak atau tidak membalas dalam 12 jam, pesanan dibatalkan otomatis tanpa biaya."
    } else {
        "Jika pemilik menolak atau tidak membalas, dana kembali penuh ke metode bayarmu secara otomatis."
    }

    KerangkaLayar(
        paddingIsi = PaddingValues(start = 21.dp, end = 21.dp, top = 89.dp, bottom = 21.dp),
        rataTengah = true,
        bawah = {
            Tombol("Lihat detail pesanan", onClick = onLihatPesanan, modifier = Modifier.fillMaxWidth())
            TautanTeks("Kembali ke Beranda", onClick = onBeranda)
        },
    ) {
        WadahIkon(R.drawable.ikon_schedule, Primary, PrimaryContainer, ukuran = 89.dp, ukuranIkon = 40.dp, radius = 28.dp)
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(judul, style = Teks.Judul, color = TextPrimary, textAlign = TextAlign.Center)
            Text(isi, style = Teks.Isi, color = TextSecondary, textAlign = TextAlign.Center)
        }
        KartuBarangSewa(draft.barang, ukuranFoto = 55.dp) {
            Text(ringkasRentang(draft), style = Teks.Keterangan, color = TextSecondary)
        }
        KotakInfo(
            R.drawable.ikon_verified_user_fill,
            jaminan,
            warna = OnPrimaryContainer,
            latar = PrimaryContainer,
            warnaIkon = Primary,
        )
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun MenungguPreview() {
    RentechTheme { MenungguKonfirmasiScreen(DraftSewa(), {}, {}) }
}
