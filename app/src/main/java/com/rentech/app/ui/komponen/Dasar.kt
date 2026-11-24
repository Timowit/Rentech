package com.rentech.app.ui.komponen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks

@Composable
fun Ikon(@DrawableRes id: Int, warna: Color, modifier: Modifier = Modifier, ukuran: Dp = 24.dp) {
    Icon(painterResource(id), contentDescription = null, tint = warna, modifier = modifier.size(ukuran))
}

/** Kotak berisi satu ikon di tengah, misalnya wadah ikon kategori atau ikon menu. */
@Composable
fun WadahIkon(
    @DrawableRes ikon: Int,
    warnaIkon: Color,
    latar: Color,
    modifier: Modifier = Modifier,
    ukuran: Dp = 44.dp,
    ukuranIkon: Dp = 22.dp,
    radius: Dp = 14.dp,
) {
    Box(
        modifier
            .size(ukuran)
            .clip(RoundedCornerShape(radius))
            .background(latar),
        contentAlignment = Alignment.Center,
    ) {
        Ikon(ikon, warnaIkon, ukuran = ukuranIkon)
    }
}

/** Latar putih bergaris tepi tipis, dipakai untuk kartu dan kotak isian. */
fun Modifier.kartu(radius: Dp = 21.dp, garis: Color = Outline, tebalGaris: Dp = 1.dp): Modifier =
    this
        .clip(RoundedCornerShape(radius))
        .background(Surface)
        .border(tebalGaris, garis, RoundedCornerShape(radius))

@Composable
fun Lencana(teks: String, warna: Color, latar: Color, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(13.dp))
            .background(latar)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(warna))
        Text(teks, style = Teks.Label, color = warna)
    }
}

/** "Langkah 1 dari 3" beserta batang progresnya. */
@Composable
fun IndikatorLangkah(langkah: Int, total: Int, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Langkah $langkah dari $total", style = Teks.Label, color = Primary)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(total) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i < langkah) Primary else Outline)
                )
            }
        }
    }
}

@Composable
fun Pemisah(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(Outline))
}

@Composable
fun JarakIsi(tinggi: Dp) = Spacer(Modifier.height(tinggi))
