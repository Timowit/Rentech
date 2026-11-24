package com.rentech.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.KolomIsian
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun MasukScreen(
    onMasuk: () -> Unit,
    onLupaSandi: () -> Unit,
    onDaftar: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var sandi by rememberSaveable { mutableStateOf("") }

    KerangkaLayar(
        paddingIsi = PaddingValues(start = 21.dp, end = 21.dp, top = 55.dp, bottom = 21.dp),
        bawah = {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Belum punya akun?", style = Teks.Isi, color = TextSecondary)
                TautanTeks("Daftar", onClick = onDaftar)
            }
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Image(painterResource(R.drawable.logo_simbol), contentDescription = "Rentech", Modifier.size(55.dp))
            Text("Masuk ke Rentech", style = Teks.Display, color = TextPrimary)
            Text(
                "Pakai akun yang sama untuk menyewa maupun menyewakan.",
                style = Teks.Isi,
                color = TextSecondary,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
            KolomIsian("Email", email, { email = it }, "nama@email.com", jenisKeyboard = KeyboardType.Email)
            KolomIsian("Kata sandi", sandi, { sandi = it }, "••••••••", sandi = true)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TautanTeks("Lupa kata sandi?", onClick = onLupaSandi)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Tombol("Masuk", onClick = onMasuk, modifier = Modifier.fillMaxWidth())
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f).height(1.dp).background(Outline))
                Text("atau", style = Teks.Keterangan, color = TextTertiary)
                Box(Modifier.weight(1f).height(1.dp).background(Outline))
            }
            Tombol(
                "Lanjut dengan Google",
                onClick = onMasuk,
                gaya = GayaTombol.Garis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun MasukPreview() {
    RentechTheme { MasukScreen({}, {}, {}) }
}
