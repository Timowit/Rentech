package com.rentech.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.IndikatorLangkah
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.KolomIsian
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

@Composable
fun DaftarScreen(
    onKembali: () -> Unit,
    onBuatAkun: () -> Unit,
    onMasuk: () -> Unit,
) {
    var nama by rememberSaveable { mutableStateOf("") }
    var hp by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var sandi by rememberSaveable { mutableStateOf("") }
    var setuju by rememberSaveable { mutableStateOf(true) }

    KerangkaLayar(
        bilahAtas = { BilahAtas("Buat akun", onKembali) },
        bawah = {
            Tombol("Buat akun", onClick = onBuatAkun, aktif = setuju, modifier = Modifier.fillMaxWidth())
            Text(
                "Sudah punya akun? Masuk",
                style = Teks.Isi,
                color = TextSecondary,
                modifier = Modifier.clickable(onClick = onMasuk),
            )
        },
    ) {
        IndikatorLangkah(langkah = 1, total = 3)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Mulai dengan akunmu", style = Teks.Judul, color = TextPrimary)
            Text("Satu akun untuk menyewa dan menyewakan.", style = Teks.Isi, color = TextSecondary)
        }

        Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
            KolomIsian(
                "Nama lengkap", nama, { nama = it }, "Sesuai KTP",
                keterangan = "Nama dicocokkan saat verifikasi identitas",
            )
            KolomIsian("Nomor HP", hp, { hp = it }, "08xx xxxx xxxx", jenisKeyboard = KeyboardType.Phone)
            KolomIsian("Email", email, { email = it }, "nama@email.com", jenisKeyboard = KeyboardType.Email)
            KolomIsian(
                "Kata sandi", sandi, { sandi = it }, "Minimal 8 karakter",
                keterangan = "Gunakan huruf dan angka",
                sandi = true,
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { setuju = !setuju },
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            KotakCentang(setuju)
            Text(
                "Saya menyetujui Syarat & Ketentuan dan Kebijakan Privasi Rentech.",
                style = Teks.Keterangan,
                color = TextSecondary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun KotakCentang(tercentang: Boolean) {
    val bentuk = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .size(22.dp)
            .clip(bentuk)
            .background(if (tercentang) Primary else Surface)
            .border(if (tercentang) 0.dp else 1.5.dp, Outline, bentuk),
        contentAlignment = Alignment.Center,
    ) {
        if (tercentang) Ikon(R.drawable.ikon_check, OnPrimary, ukuran = 16.dp)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun DaftarPreview() {
    RentechTheme { DaftarScreen({}, {}, {}) }
}
