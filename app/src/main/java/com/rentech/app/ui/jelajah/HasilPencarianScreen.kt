package com.rentech.app.ui.jelajah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private enum class Urutan(val label: String) { Terdekat("Terdekat"), Termurah("Termurah") }

@Composable
fun HasilPencarianScreen(
    kueri: String,
    bukaFilter: Boolean,
    onKembali: () -> Unit,
    onUbahKueri: () -> Unit,
    onBarang: (Barang) -> Unit,
) {
    var filter by remember { mutableStateOf(FilterCari()) }
    var tampilFilter by rememberSaveable { mutableStateOf(bukaFilter) }
    var tersediaHariIni by rememberSaveable { mutableStateOf(true) }
    var urutan by rememberSaveable { mutableStateOf(Urutan.Terdekat) }

    val hasil = saringBarang(kueri, filter).let { daftar ->
        when (urutan) {
            Urutan.Terdekat -> daftar.sortedBy { it.jarak.removeSuffix(" km").replace(',', '.').toFloat() }
            Urutan.Termurah -> daftar.sortedBy { it.hargaPerHari }
        }
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 13.dp, end = 21.dp, top = 13.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TombolKembaliPolos(onKembali)
            KolomCari(kueri, onUbah = {}, onCari = {}, modifier = Modifier.weight(1f), onKetuk = onUbahKueri)
        }
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(21.dp))
                    .background(Primary)
                    .clickable { tampilFilter = true }
                    .padding(start = 12.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ikon(R.drawable.ikon_tune, OnPrimary, ukuran = 16.dp)
                val n = filter.jumlahAktif
                Text(if (n > 0) "Filter · $n" else "Filter", style = Teks.Label, color = OnPrimary)
            }
            Chip("Terdekat", terpilih = urutan == Urutan.Terdekat, onClick = { urutan = Urutan.Terdekat })
            Chip("Tersedia hari ini", terpilih = tersediaHariIni, onClick = { tersediaHariIni = !tersediaHariIni })
            Chip("Harga", terpilih = urutan == Urutan.Termurah, onClick = { urutan = Urutan.Termurah })
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${hasil.size} barang di sekitar Jakarta Selatan",
                    style = Teks.Isi,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f),
                )
                Row(
                    Modifier.clickable {
                        urutan = if (urutan == Urutan.Terdekat) Urutan.Termurah else Urutan.Terdekat
                    },
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(urutan.label, style = Teks.IsiTebal, color = TextPrimary)
                    Ikon(R.drawable.ikon_keyboard_arrow_down, TextPrimary, ukuran = 18.dp)
                }
            }
            if (hasil.isEmpty()) {
                Text(
                    "Belum ada barang yang cocok. Coba kata lain atau longgarkan filter.",
                    style = Teks.Isi,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
                )
            }
            hasil.chunked(2).forEach { baris ->
                Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    baris.forEach { b -> KartuBarang(b, onClick = { onBarang(b) }, modifier = Modifier.weight(1f)) }
                    if (baris.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }

    if (tampilFilter) {
        FilterSheet(
            kueri = kueri,
            awal = filter,
            onTutup = { tampilFilter = false },
            onTerapkan = { filter = it; tampilFilter = false },
        )
    }
}

@Preview(heightDp = 928, widthDp = 412)
@Composable
private fun HasilPreview() {
    RentechTheme { HasilPencarianScreen("", false, {}, {}, {}) }
}
