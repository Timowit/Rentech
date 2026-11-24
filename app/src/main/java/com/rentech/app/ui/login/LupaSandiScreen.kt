package com.rentech.app.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.KolomIsian
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun LupaSandiScreen(
    onKembali: () -> Unit,
    onKirimTautan: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }

    KerangkaLayar(
        bilahAtas = { BilahAtas("", onKembali) },
        paddingIsi = PaddingValues(21.dp),
        bawah = {
            Tombol("Kirim tautan", onClick = onKirimTautan, modifier = Modifier.fillMaxWidth())
            TautanTeks("Kembali ke Masuk", onClick = onKembali)
        },
    ) {
        WadahIkon(
            R.drawable.ikon_lock, Primary, PrimaryContainer,
            ukuran = 72.dp, ukuranIkon = 32.dp, radius = 23.dp,
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Lupa kata sandi?", style = Teks.Judul, color = TextPrimary)
            Text(
                "Masukkan email akunmu. Kami kirim tautan untuk membuat kata sandi baru.",
                style = Teks.Isi,
                color = TextSecondary,
            )
        }
        KolomIsian("Email", email, { email = it }, "nama@email.com", jenisKeyboard = KeyboardType.Email)
    }
}

@Composable
fun AturUlangSandiScreen(
    onKembali: () -> Unit,
    onSimpan: () -> Unit,
) {
    var sandi by rememberSaveable { mutableStateOf("") }
    var ulangi by rememberSaveable { mutableStateOf("") }

    val cukupPanjang = sandi.length >= 8
    val hurufDanAngka = sandi.any(Char::isLetter) && sandi.any(Char::isDigit)
    val sama = sandi.isNotEmpty() && sandi == ulangi

    KerangkaLayar(
        bilahAtas = { BilahAtas("Kata sandi baru", onKembali) },
        bawah = {
            Tombol(
                "Simpan kata sandi",
                onClick = onSimpan,
                aktif = cukupPanjang && hurufDanAngka && sama,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Buat kata sandi baru", style = Teks.Judul, color = TextPrimary)
            Text(
                "Setelah disimpan, kamu akan keluar dari perangkat lain.",
                style = Teks.Isi,
                color = TextSecondary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
            KolomIsian("Kata sandi baru", sandi, { sandi = it }, "••••••••••", sandi = true)
            KolomIsian("Ulangi kata sandi", ulangi, { ulangi = it }, "••••••••••", sandi = true)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .kartu(radius = 18.dp)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SyaratSandi("Minimal 8 karakter", cukupPanjang)
            SyaratSandi("Mengandung huruf dan angka", hurufDanAngka)
            SyaratSandi("Kedua kata sandi sama", sama)
        }
    }
}

@Composable
private fun SyaratSandi(teks: String, terpenuhi: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Ikon(
            if (terpenuhi) R.drawable.ikon_check_circle_fill else R.drawable.ikon_check_circle,
            if (terpenuhi) Success else Outline,
            ukuran = 18.dp,
        )
        Text(teks, style = Teks.Isi, color = if (terpenuhi) TextPrimary else TextTertiary)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun LupaSandiPreview() {
    RentechTheme { LupaSandiScreen({}, {}) }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun AturUlangSandiPreview() {
    RentechTheme { AturUlangSandiScreen({}, {}) }
}
