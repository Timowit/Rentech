package com.rentech.app.ui.jelajah

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.contohBarang
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.GayaTombol
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary

const val HARGA_TERENDAH = 25_000f
const val HARGA_TERTINGGI = 400_000f

val pilihanJarak = listOf("≤ 2 km" to 2f, "≤ 5 km" to 5f, "≤ 10 km" to 10f, "Semua" to Float.MAX_VALUE)
private val kategoriFilter = listOf("Kamera", "Laptop", "Proyektor", "Drone", "Audio", "Gimbal")
private val pilihanKondisi = listOf("Baru", "Sangat baik", "Baik")

data class FilterCari(
    val kategori: Set<String> = emptySet(),
    val hargaMin: Float = HARGA_TERENDAH,
    val hargaMax: Float = HARGA_TERTINGGI,
    val jarak: Int = 1,
    val kondisi: Set<String> = emptySet(),
) {
    /** Jumlah kelompok filter yang sedang aktif, untuk label "Filter · 2". */
    val jumlahAktif: Int
        get() = listOf(
            kategori.isNotEmpty(),
            hargaMin > HARGA_TERENDAH || hargaMax < HARGA_TERTINGGI,
            jarak != pilihanJarak.lastIndex,
            kondisi.isNotEmpty(),
        ).count { it }
}

private fun jarakKm(b: Barang) = b.jarak.removeSuffix(" km").replace(',', '.').toFloat()

/** Barang yang cocok dengan kueri dan filter. Kondisi belum ada di data contoh, jadi belum ikut menyaring. */
fun saringBarang(kueri: String, f: FilterCari): List<Barang> = contohBarang.filter { b ->
    val k = kueri.trim()
    (k.isEmpty() || b.nama.contains(k, true) || b.kategori.contains(k, true)) &&
        (f.kategori.isEmpty() || b.kategori in f.kategori) &&
        b.hargaPerHari in f.hargaMin.toLong()..f.hargaMax.toLong() &&
        jarakKm(b) <= pilihanJarak[f.jarak].second
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(kueri: String, awal: FilterCari, onTutup: () -> Unit, onTerapkan: (FilterCari) -> Unit) {
    var f by remember { mutableStateOf(awal) }
    val jumlah = saringBarang(kueri, f).size
    ModalBottomSheet(
        onDismissRequest = onTutup,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RectangleShape,
        containerColor = Surface,
        scrimColor = Color(0x730A0D12),
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 13.dp)
                    .size(34.dp, 5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Outline)
            )
        },
    ) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(start = 21.dp, end = 21.dp, top = 21.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Filter", style = Teks.Judul, color = TextPrimary, modifier = Modifier.weight(1f))
                Ikon(R.drawable.ikon_close, TextPrimary, Modifier.clickable(onClick = onTutup), ukuran = 22.dp)
            }
            Kelompok("Kategori") {
                kategoriFilter.forEach { k ->
                    Chip(k, terpilih = k in f.kategori, onClick = {
                        f = f.copy(kategori = if (k in f.kategori) f.kategori - k else f.kategori + k)
                    })
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Harga per hari", style = Teks.IsiTebal, color = TextPrimary)
                    Text(
                        "${rupiah(f.hargaMin.toLong())} – ${rupiah(f.hargaMax.toLong())}",
                        style = Teks.Isi,
                        color = Primary,
                    )
                }
                RentangHarga(f.hargaMin, f.hargaMax) { min, max -> f = f.copy(hargaMin = min, hargaMax = max) }
            }
            Kelompok("Jarak") {
                pilihanJarak.forEachIndexed { i, (label, _) ->
                    Chip(label, terpilih = i == f.jarak, onClick = { f = f.copy(jarak = i) })
                }
            }
            Kelompok("Kondisi") {
                pilihanKondisi.forEach { k ->
                    Chip(k, terpilih = k in f.kondisi, onClick = {
                        f = f.copy(kondisi = if (k in f.kondisi) f.kondisi - k else f.kondisi + k)
                    })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Tombol("Atur ulang", onClick = { f = FilterCari(jarak = pilihanJarak.lastIndex) }, gaya = GayaTombol.Garis)
                Tombol(
                    "Tampilkan $jumlah barang",
                    onClick = { onTerapkan(f) },
                    modifier = Modifier.weight(1f),
                    aktif = jumlah > 0,
                )
            }
        }
    }
}

@Composable
private fun Kelompok(judul: String, isi: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text(judul, style = Teks.IsiTebal, color = TextPrimary)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            isi()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RentangHarga(min: Float, max: Float, onUbah: (Float, Float) -> Unit) {
    val rentang = HARGA_TERENDAH..HARGA_TERTINGGI
    RangeSlider(
        value = min..max,
        onValueChange = { onUbah(it.start, it.endInclusive) },
        valueRange = rentang,
        steps = 14,
        modifier = Modifier.fillMaxWidth().height(24.dp),
        startThumb = { Jempol() },
        endThumb = { Jempol() },
        track = { s ->
            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val lebar = rentang.endInclusive - rentang.start
                val a = (s.activeRangeStart - rentang.start) / lebar * size.width
                val b = (s.activeRangeEnd - rentang.start) / lebar * size.width
                val y = size.height / 2
                drawLine(Outline, Offset(0f, y), Offset(size.width, y), size.height, StrokeCap.Round)
                drawLine(Primary, Offset(a, y), Offset(b, y), size.height, StrokeCap.Round)
            }
        },
    )
}

@Composable
private fun Jempol() {
    Box(
        Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Surface)
            .border(3.dp, Primary, CircleShape)
    )
}
