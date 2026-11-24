package com.rentech.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KerangkaLayar
import com.rentech.app.ui.komponen.TautanTeks
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import kotlinx.coroutines.delay

private const val PANJANG_KODE = 6

@Composable
fun OtpScreen(
    nomorHp: String,
    onKembali: () -> Unit,
    onVerifikasi: () -> Unit,
    onGantiNomor: () -> Unit,
) {
    var kode by rememberSaveable { mutableStateOf("") }
    var sisaDetik by rememberSaveable { mutableIntStateOf(45) }
    val fokus = remember { FocusRequester() }

    LaunchedEffect(Unit) { fokus.requestFocus() }
    LaunchedEffect(sisaDetik) {
        if (sisaDetik > 0) {
            delay(1000)
            sisaDetik--
        }
    }

    KerangkaLayar(
        bilahAtas = { BilahAtas("", onKembali) },
        paddingIsi = PaddingValues(21.dp),
        bawah = {
            Tombol(
                "Verifikasi",
                onClick = onVerifikasi,
                aktif = kode.length == PANJANG_KODE,
                modifier = Modifier.fillMaxWidth(),
            )
            TautanTeks("Ganti nomor HP", onClick = onGantiNomor)
        },
    ) {
        WadahIkon(
            R.drawable.ikon_mail, Primary, PrimaryContainer,
            ukuran = 72.dp, ukuranIkon = 32.dp, radius = 23.dp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Masukkan kode verifikasi", style = Teks.Judul, color = TextPrimary)
            Text("Kami mengirim 6 digit kode ke $nomorHp.", style = Teks.Isi, color = TextSecondary)
        }

        BasicTextField(
            value = kode,
            onValueChange = { baru -> kode = baru.filter(Char::isDigit).take(PANJANG_KODE) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.focusRequester(fokus),
            decorationBox = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(PANJANG_KODE) { i ->
                        KotakDigit(
                            digit = kode.getOrNull(i)?.toString(),
                            aktif = i == kode.length,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            },
        )

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            if (sisaDetik > 0) {
                Ikon(R.drawable.ikon_schedule, TextTertiary, ukuran = 16.dp)
                Text(
                    "Kirim ulang kode dalam 00:%02d".format(sisaDetik),
                    style = Teks.Isi,
                    color = TextTertiary,
                )
            } else {
                TautanTeks("Kirim ulang kode", onClick = { sisaDetik = 45 })
            }
        }
    }
}

@Composable
private fun KotakDigit(digit: String?, aktif: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(62.dp)
            .kartu(radius = 16.dp, garis = if (aktif) Primary else Outline, tebalGaris = if (aktif) 2.dp else 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        when {
            digit != null -> Text(digit, style = Teks.Judul, color = TextPrimary)
            aktif -> Box(Modifier.width(2.dp).height(24.dp).background(Primary))
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun OtpPreview() {
    RentechTheme { OtpScreen("0812 •••• 7890", {}, {}, {}) }
}
