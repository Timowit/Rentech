package com.rentech.app.ui.komponen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

/** Bilah putih di bawah layar, berisi tombol aksi utama. */
@Composable
fun BilahAksi(modifier: Modifier = Modifier, isi: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Surface)
            .drawBehind { drawLine(Outline, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .navigationBarsPadding()
            .padding(start = 21.dp, end = 21.dp, top = 13.dp, bottom = 21.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = isi,
    )
}

/** Cip pilihan, misalnya kategori, jarak, atau kondisi barang. */
@Composable
fun Chip(
    label: String,
    terpilih: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
) {
    val bentuk = RoundedCornerShape(21.dp)
    Text(
        label,
        style = Teks.Label,
        color = if (terpilih) OnPrimaryContainer else TextSecondary,
        modifier = modifier
            .clip(bentuk)
            .background(if (terpilih) PrimaryContainer else Surface)
            .border(if (terpilih) 1.5.dp else 1.dp, if (terpilih) Primary else Outline, bentuk)
            .clickable(onClick = onClick)
            .padding(padding),
    )
}

/** Tombol ikon bulat, misalnya tombol notifikasi atau simpan. */
@Composable
fun TombolBulat(
    @DrawableRes ikon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ukuran: Dp = 40.dp,
    ukuranIkon: Dp = 20.dp,
    bergaris: Boolean = true,
    latar: Color = Surface,
    warnaIkon: Color = TextPrimary,
) {
    val bentuk = CircleShape
    var m = modifier.size(ukuran).clip(bentuk).background(latar)
    if (bergaris) m = m.border(1.dp, Outline, bentuk)
    Box(m.clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Ikon(ikon, warnaIkon, ukuran = ukuranIkon)
    }
}

/** Kotak keterangan berlatar warna lembut dengan ikon di kiri. */
@Composable
fun KotakInfo(
    @DrawableRes ikon: Int,
    teks: String,
    warna: Color,
    latar: Color,
    modifier: Modifier = Modifier,
    judul: String? = null,
    warnaIkon: Color = warna,
    ukuranIkon: Dp = 20.dp,
    radius: Dp = 18.dp,
    gayaTeks: TextStyle = Teks.Keterangan,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .background(latar)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Ikon(ikon, warnaIkon, ukuran = ukuranIkon)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            if (judul != null) Text(judul, style = Teks.IsiTebal, color = warna)
            Text(teks, style = gayaTeks, color = warna)
        }
    }
}

/** Kotak centang. */
@Composable
fun KotakCentang(tercentang: Boolean, modifier: Modifier = Modifier, ukuran: Dp = 22.dp, radius: Dp = 6.dp) {
    val bentuk = RoundedCornerShape(radius)
    Box(
        modifier
            .size(ukuran)
            .clip(bentuk)
            .background(if (tercentang) Primary else Surface)
            .border(if (tercentang) 0.dp else 1.5.dp, Outline, bentuk),
        contentAlignment = Alignment.Center,
    ) {
        if (tercentang) Ikon(R.drawable.ikon_check, OnPrimary, ukuran = 16.dp)
    }
}

/** Lingkaran pilihan tunggal. */
@Composable
fun Radio(terpilih: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(Surface)
            .border(2.dp, if (terpilih) Primary else Outline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (terpilih) Box(Modifier.size(12.dp).clip(CircleShape).background(Primary))
    }
}

/** "Rp350.000/hari" dengan satuan yang lebih kecil. */
@Composable
fun HargaPerHari(
    harga: String,
    modifier: Modifier = Modifier,
    gaya: TextStyle = Teks.IsiTebal,
    warna: Color = Primary,
    gayaSatuan: TextStyle = Teks.Keterangan,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(harga, style = gaya, color = warna, modifier = Modifier.alignByBaseline())
        Text("/hari", style = gayaSatuan, color = TextTertiary, modifier = Modifier.alignByBaseline())
    }
}

/** Satu baris rincian biaya: label di kiri, nilai di kanan. */
@Composable
fun BarisRincian(
    label: String,
    nilai: String,
    modifier: Modifier = Modifier,
    tebal: Boolean = false,
    gayaNilai: TextStyle = if (tebal) Teks.IsiTebal else Teks.Isi,
    warnaNilai: Color = TextPrimary,
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = if (tebal) Teks.IsiTebal else Teks.Isi,
            color = if (tebal) TextPrimary else TextSecondary,
            modifier = Modifier.weight(1f),
        )
        Text(nilai, style = gayaNilai, color = warnaNilai)
    }
}
