package com.rentech.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.contohBarang
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.pemilik.Avatar
import com.rentech.app.ui.pemilik.FotoBarang
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/** Satu pesan di percakapan. [keluar] berarti dikirim oleh pengguna. */
data class Pesan(val teks: String, val waktu: String, val keluar: Boolean)

/** Contoh percakapan saat pesanan sudah diterima pemilik. */
private val percakapanSewa = listOf(
    Pesan("Halo, pesanan kameranya sudah saya terima. Mau diambil jam berapa?", "09.20", keluar = false),
    Pesan("Jam 10 pagi bisa? Saya ambil sendiri ke Kemang.", "09.24", keluar = true),
    Pesan("Bisa. Kameranya sudah saya cas penuh dan kartu memorinya sudah dikosongkan.", "09.31", keluar = false),
    Pesan("Siap, terima kasih!", "09.32 · Dibaca", keluar = true),
)

private val balasanCepat = listOf("Saya sudah sampai", "Bisa geser 30 menit?", "Terima kasih")
private val formatJam = DateTimeFormatter.ofPattern("HH.mm")

/**
 * Chat dengan pemilik barang.
 * [keterangan] tampil di bawah nama barang, misalnya rentang tanggal sewa.
 * [danaDitahan] menentukan lencana pesanan; null berarti belum ada pesanan.
 */
@Composable
fun ChatPemilikScreen(
    barang: Barang,
    keterangan: String,
    danaDitahan: Boolean?,
    onKembali: () -> Unit,
) {
    val adaPesanan = danaDitahan != null
    val pesan = remember { mutableStateListOf<Pesan>().apply { if (adaPesanan) addAll(percakapanSewa) } }
    var draf by rememberSaveable { mutableStateOf("") }
    val daftar = rememberLazyListState()

    fun kirim(teks: String) {
        val bersih = teks.trim()
        if (bersih.isEmpty()) return
        pesan.add(Pesan(bersih, LocalTime.now().format(formatJam), keluar = true))
        draf = ""
    }

    LaunchedEffect(pesan.size) {
        if (pesan.isNotEmpty()) daftar.animateScrollToItem(daftar.layoutInfo.totalItemsCount - 1)
    }

    Column(Modifier.fillMaxSize().background(Background).imePadding()) {
        BilahChat(barang, onKembali)
        KartuPesanan(barang, keterangan, danaDitahan)
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            state = daftar,
            contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 21.dp, bottom = 13.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "Hari ini",
                        style = Teks.Label,
                        color = TextSecondary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .background(SurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
            if (!adaPesanan) item { CatatanAman() }
            itemsIndexed(pesan) { i, p ->
                Gelembung(p)
                if (adaPesanan && i == 2) {
                    Box(Modifier.padding(top = 8.dp)) { CatatanAman() }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, bottom = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            balasanCepat.forEach { Chip(it, terpilih = false, onClick = { kirim(it) }) }
        }
        KolomPesan(draf, onUbah = { draf = it }, onKirim = { kirim(draf) })
    }
}

@Composable
private fun BilahChat(barang: Barang, onKembali: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 21.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TombolBulat(R.drawable.ikon_arrow_back, onKembali, ukuranIkon = 22.dp)
        Avatar(R.drawable.avatar_2, 40.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Pemilik terverifikasi", style = Teks.IsiTebal, color = TextPrimary)
                Ikon(R.drawable.ikon_verified_fill, Primary, ukuran = 16.dp)
            }
            Text(
                "Pemilik ${barang.nama.replaceFirstChar { it.lowercase() }}",
                style = Teks.Keterangan,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun KartuPesanan(barang: Barang, keterangan: String, danaDitahan: Boolean?) {
    Row(
        Modifier
            .padding(horizontal = 21.dp)
            .fillMaxWidth()
            .kartu(radius = 18.dp)
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FotoBarang(barang.foto, 44.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(barang.nama, style = Teks.IsiTebal, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(keterangan, style = Teks.Keterangan, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        when (danaDitahan) {
            true -> Lencana("Dana ditahan", OnPrimaryContainer, PrimaryContainer)
            false -> Lencana("Bayar tunai", Warning, WarningContainer)
            null -> {}
        }
    }
}

@Composable
private fun Gelembung(p: Pesan) {
    val bentuk = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (p.keluar) 18.dp else 6.dp,
        bottomEnd = if (p.keluar) 6.dp else 18.dp,
    )
    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = if (p.keluar) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Column(
            Modifier
                .widthIn(max = 290.dp)
                .clip(bentuk)
                .background(if (p.keluar) Primary else Surface)
                .then(if (p.keluar) Modifier else Modifier.border(1.dp, Outline, bentuk))
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(p.teks, style = Teks.Isi, color = if (p.keluar) OnPrimary else TextPrimary)
            Text(
                p.waktu,
                style = Teks.Keterangan,
                color = if (p.keluar) OnPrimary.copy(alpha = 0.72f) else TextTertiary,
            )
        }
    }
}

@Composable
private fun CatatanAman() {
    KotakInfo(
        R.drawable.ikon_lock,
        "Jangan bayar di luar aplikasi. Dana kamu aman selama pembayaran lewat Rentech.",
        warna = TextSecondary,
        latar = SurfaceVariant,
        ukuranIkon = 16.dp,
        radius = 13.dp,
    )
}

/** Kolom tulis pesan dengan tombol lampiran dan kirim. */
@Composable
fun KolomPesan(
    teks: String,
    onUbah: (String) -> Unit,
    onKirim: () -> Unit,
    petunjuk: String = "Tulis pesan…",
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Surface)
            .drawBehind { drawLine(Outline, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .navigationBarsPadding()
            .padding(start = 21.dp, end = 21.dp, top = 13.dp, bottom = 21.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TombolBulat(R.drawable.ikon_add, onClick = {}, ukuran = 44.dp, ukuranIkon = 22.dp, bergaris = false, latar = SurfaceVariant, warnaIkon = TextSecondary)
        BasicTextField(
            value = teks,
            onValueChange = onUbah,
            textStyle = Teks.Isi.copy(color = TextPrimary),
            cursorBrush = SolidColor(Primary),
            maxLines = 4,
            modifier = Modifier.weight(1f),
            decorationBox = { kolom ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Background)
                        .border(1.dp, Outline, RoundedCornerShape(22.dp))
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (teks.isEmpty()) Text(petunjuk, style = Teks.Isi, color = TextTertiary)
                    kolom()
                }
            },
        )
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Primary)
                .clickable(enabled = teks.isNotBlank(), onClick = onKirim),
            contentAlignment = Alignment.Center,
        ) {
            Ikon(R.drawable.ikon_send, OnPrimary, ukuran = 20.dp)
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun ChatPemilikPreview() {
    RentechTheme { ChatPemilikScreen(contohBarang.first(), "Sab, 12 – Sel, 15 Des · 3 hari", danaDitahan = true, onKembali = {}) }
}
