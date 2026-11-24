package com.rentech.app.ui.komponen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary

enum class GayaTombol { Utama, Garis, Lembut }

@Composable
fun Tombol(
    teks: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gaya: GayaTombol = GayaTombol.Utama,
    aktif: Boolean = true,
    tinggi: Dp = 55.dp,
) {
    val bentuk = RoundedCornerShape(18.dp)
    val (latar, warnaTeks) = when (gaya) {
        GayaTombol.Utama -> Primary to OnPrimary
        GayaTombol.Garis -> Surface to TextPrimary
        GayaTombol.Lembut -> PrimaryContainer to OnPrimaryContainer
    }
    var m = modifier
        .height(tinggi)
        .alpha(if (aktif) 1f else 0.4f)
        .clip(bentuk)
        .background(latar)
    if (gaya == GayaTombol.Garis) m = m.border(1.dp, Outline, bentuk)
    Box(
        m.clickable(enabled = aktif, onClick = onClick).padding(horizontal = 21.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(teks, style = Teks.Tombol, color = warnaTeks)
    }
}

/** Tombol teks biru tanpa latar, misalnya "Lupa kata sandi?". */
@Composable
fun TautanTeks(teks: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        teks,
        style = Teks.IsiTebal,
        color = Primary,
        modifier = modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick),
    )
}
