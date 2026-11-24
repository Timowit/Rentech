package com.rentech.app.ui.jelajah

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.kategoriBarang
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private data class Saran(val teks: String, val kategori: String?)

private val daftarSaran = listOf(
    Saran("kamera mirrorless", "Kamera"),
    Saran("kamera aksi", "Kamera"),
    Saran("kamera untuk vlog", null),
    Saran("laptop 14 inci", "Laptop"),
    Saran("laptop untuk desain", null),
    Saran("proyektor portabel", "Proyektor"),
    Saran("drone lipat", "Drone"),
    Saran("headphone peredam bising", "Audio"),
    Saran("konsol game", "Konsol game"),
    Saran("gimbal stabilizer", "Gimbal"),
)

@Composable
fun PencarianScreen(
    kueriAwal: String,
    onKembali: () -> Unit,
    onCari: (String) -> Unit,
    onKategori: (String) -> Unit,
) {
    var kueri by rememberSaveable { mutableStateOf(kueriAwal) }
    val terakhir = remember { mutableStateListOf("proyektor portabel", "drone lipat", "laptop 14 inci") }
    val saran = kueri.trim().let { k ->
        if (k.isEmpty()) emptyList() else daftarSaran.filter { it.teks.contains(k, ignoreCase = true) }.take(5)
    }
    val cari = { teks: String ->
        val t = teks.trim()
        if (t.isNotEmpty()) {
            terakhir.remove(t)
            terakhir.add(0, t)
            onCari(t)
        }
    }

    Column(Modifier.fillMaxSize().background(Background).imePadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 13.dp, end = 21.dp, top = 13.dp, bottom = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TombolKembaliPolos(onKembali)
            KolomCari(
                nilai = kueri,
                onUbah = { kueri = it },
                onCari = { cari(kueri) },
                modifier = Modifier.weight(1f),
            )
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            if (saran.isNotEmpty()) {
                Column {
                    saran.forEach { s -> BarisSaran(s, kueri.trim(), onClick = { cari(s.teks) }) }
                }
            }
            if (terakhir.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pencarian terakhir", style = Teks.IsiTebal, color = TextPrimary)
                        Text(
                            "Hapus",
                            style = Teks.IsiTebal,
                            color = Primary,
                            modifier = Modifier.clickable { terakhir.clear() },
                        )
                    }
                    terakhir.toList().forEach { t ->
                        Row(
                            Modifier.fillMaxWidth().clickable { cari(t) },
                            horizontalArrangement = Arrangement.spacedBy(13.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Ikon(R.drawable.ikon_history, TextTertiary, ukuran = 20.dp)
                            Text(t, style = Teks.Isi, color = TextSecondary, modifier = Modifier.weight(1f))
                            Ikon(
                                R.drawable.ikon_close,
                                TextTertiary,
                                Modifier.clickable { terakhir.remove(t) },
                                ukuran = 18.dp,
                            )
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text("Jelajahi kategori", style = Teks.IsiTebal, color = TextPrimary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    kategoriBarang.forEach { k -> Chip(k, terpilih = false, onClick = { onKategori(k) }) }
                }
            }
        }
    }
}

@Composable
private fun BarisSaran(s: Saran, kueri: String, onClick: () -> Unit) {
    val ikon = if (s.kategori != null) ikonKategori(s.kategori) else R.drawable.ikon_search
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ikon(ikon, TextTertiary, ukuran = 20.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(tebalkanKueri(s.teks, kueri), style = Teks.Isi, color = TextPrimary)
            if (s.kategori != null) Text("Kategori ${s.kategori}", style = Teks.Keterangan, color = TextTertiary)
        }
        Ikon(R.drawable.ikon_arrow_forward, TextTertiary, ukuran = 18.dp)
    }
}

/** Bagian teks yang sama dengan kueri ditulis tebal. */
private fun tebalkanKueri(teks: String, kueri: String) = buildAnnotatedString {
    val i = teks.indexOf(kueri, ignoreCase = true)
    if (kueri.isEmpty() || i < 0) {
        append(teks)
    } else {
        append(teks.substring(0, i))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(teks.substring(i, i + kueri.length)) }
        append(teks.substring(i + kueri.length))
    }
}

@DrawableRes
fun ikonKategori(kategori: String): Int = when (kategori) {
    "Kamera" -> R.drawable.ikon_photo_camera
    "Laptop" -> R.drawable.ikon_laptop_mac
    "Proyektor" -> R.drawable.ikon_projector
    "Drone" -> R.drawable.ikon_drone
    "Audio" -> R.drawable.ikon_headphones
    else -> R.drawable.ikon_search
}

@Composable
fun TombolKembaliPolos(onKembali: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Background)
            .clickable(onClick = onKembali),
        contentAlignment = Alignment.Center,
    ) {
        Ikon(R.drawable.ikon_arrow_back, TextPrimary, ukuran = 22.dp)
    }
}

/** Kolom pencarian. Jika [onKetuk] diisi, kolom hanya menampilkan kueri dan membuka layar pencarian saat diketuk. */
@Composable
fun KolomCari(
    nilai: String,
    onUbah: (String) -> Unit,
    onCari: () -> Unit,
    modifier: Modifier = Modifier,
    onKetuk: (() -> Unit)? = null,
) {
    val fokus = remember { FocusRequester() }
    if (onKetuk == null) LaunchedEffect(Unit) { fokus.requestFocus() }
    val aktif = onKetuk == null
    val bentuk = RoundedCornerShape(18.dp)
    Row(
        modifier
            .height(55.dp)
            .clip(bentuk)
            .background(Surface)
            .border(if (aktif) 2.dp else 1.dp, if (aktif) Primary else Outline, bentuk)
            .then(if (onKetuk != null) Modifier.clickable(onClick = onKetuk) else Modifier)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ikon(R.drawable.ikon_search, if (aktif) Primary else TextTertiary, ukuran = 22.dp)
        Box(Modifier.weight(1f)) {
            if (onKetuk != null) {
                Text(nilai, style = Teks.Isi, color = TextPrimary, maxLines = 1)
            } else {
                if (nilai.isEmpty()) Text("Cari kamera, laptop, drone…", style = Teks.Isi, color = TextTertiary)
                BasicTextField(
                    value = nilai,
                    onValueChange = onUbah,
                    singleLine = true,
                    textStyle = Teks.Isi.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onCari() }),
                    modifier = Modifier.fillMaxWidth().focusRequester(fokus),
                )
            }
        }
        if (nilai.isNotEmpty()) {
            Ikon(
                R.drawable.ikon_close,
                TextTertiary,
                Modifier.clickable { if (onKetuk != null) onKetuk() else onUbah("") },
                ukuran = 20.dp,
            )
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun PencarianPreview() {
    RentechTheme { PencarianScreen("kam", {}, {}, {}) }
}
