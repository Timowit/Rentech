package com.rentech.app.ui.pemilik

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

enum class StatusPermintaan { Baru, Menunggu }

data class Permintaan(
    val id: Int,
    val namaBarang: String,
    @DrawableRes val fotoBarang: Int,
    @DrawableRes val avatarPenyewa: Int,
    val tanggalSingkat: String,
    val tanggalLengkap: String,
    val total: String,
    val status: StatusPermintaan,
)

val contohPermintaan = listOf(
    Permintaan(
        1, "Kamera mirrorless full-frame", R.drawable.foto_kamera, R.drawable.avatar_3,
        "12 – 15 Des 2026 · 3 hari", "Sab, 12 Des – Sel, 15 Des 2026 · 3 hari",
        "Rp1.050.000", StatusPermintaan.Baru,
    ),
    Permintaan(
        2, "Proyektor portabel", R.drawable.foto_proyektor, R.drawable.avatar_4,
        "19 Des 2026 · 1 hari", "Sab, 19 Des 2026 · 1 hari",
        "Rp120.000", StatusPermintaan.Menunggu,
    ),
)

@Composable
fun FotoBarang(@DrawableRes foto: Int, ukuran: Dp, modifier: Modifier = Modifier) {
    Image(
        painterResource(foto),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(ukuran)
            .clip(RoundedCornerShape(13.dp)),
    )
}

@Composable
fun Avatar(@DrawableRes foto: Int, ukuran: Dp, modifier: Modifier = Modifier) {
    Image(
        painterResource(foto),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(ukuran)
            .clip(CircleShape),
    )
}

@Composable
fun PenyewaTerverifikasi(@DrawableRes avatar: Int, ukuranAvatar: Dp, modifier: Modifier = Modifier) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(avatar, ukuranAvatar)
        Text("Penyewa terverifikasi", style = Teks.Label, color = TextPrimary)
        Ikon(R.drawable.ikon_verified_fill, Primary, ukuran = 16.dp)
    }
}

@Composable
fun RingkasBarang(@DrawableRes foto: Int, nama: String, keterangan: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FotoBarang(foto, 55.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(nama, style = Teks.IsiTebal, color = TextPrimary)
            Text(keterangan, style = Teks.Keterangan, color = TextSecondary)
        }
    }
}
