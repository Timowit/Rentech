package com.rentech.app.ui.sewa

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.data.Barang
import com.rentech.app.ui.komponen.BilahAksi
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Radio
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.pemilik.FotoBarang
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

/** Kerangka layar alur sewa: bilah atas, isi bergulir, dan bilah aksi di bawah. */
@Composable
fun KerangkaSewa(
    judul: String,
    onKembali: () -> Unit,
    aksi: @Composable ColumnScope.() -> Unit,
    isi: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Background)) {
        BilahAtas(judul, onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
            content = isi,
        )
        BilahAksi(isi = aksi)
    }
}

/** Bilah aksi berisi ringkasan di kiri dan tombol di kanan. */
@Composable
fun AksiDenganRingkasan(label: String, nilai: String, teksTombol: String, aktif: Boolean = true, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = Teks.Keterangan, color = TextSecondary)
            Text(nilai, style = Teks.JudulBilah, color = TextPrimary)
        }
        Tombol(teksTombol, onClick = onClick, modifier = Modifier.width(160.dp), aktif = aktif)
    }
}

/** Kartu pilihan tunggal, misalnya cara serah terima atau metode pembayaran. */
@Composable
fun KartuOpsi(
    @DrawableRes ikon: Int,
    judul: String,
    keterangan: String,
    terpilih: Boolean,
    onClick: () -> Unit,
    kanan: String? = null,
    tengahVertikal: Boolean = true,
    tambahan: @Composable () -> Unit = {},
) {
    val bentuk = RoundedCornerShape(21.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(bentuk)
            .then(
                if (terpilih) Modifier.background(PrimaryContainer).border(2.dp, Primary, bentuk)
                else Modifier.kartu()
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = if (tengahVertikal) Alignment.CenterVertically else Alignment.Top,
    ) {
        WadahIkon(
            ikon,
            warnaIkon = if (terpilih) OnPrimary else TextPrimary,
            latar = if (terpilih) Primary else SurfaceVariant,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (tengahVertikal) 3.dp else 5.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(keterangan, style = Teks.Keterangan, color = TextSecondary)
            tambahan()
        }
        if (kanan != null) Text(kanan, style = Teks.IsiTebal, color = if (terpilih) Primary else TextPrimary)
        Radio(terpilih)
    }
}

/** Kartu ringkas barang yang sedang disewa: foto dan nama. */
@Composable
fun KartuBarangSewa(
    barang: Barang,
    ukuranFoto: Dp = 66.dp,
    bawah: @Composable ColumnScope.() -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .kartu()
            .padding(start = 13.dp, end = 21.dp, top = 13.dp, bottom = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FotoBarang(barang.foto, ukuranFoto)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(barang.nama, style = Teks.IsiTebal, color = TextPrimary)
            bawah()
        }
    }
}
