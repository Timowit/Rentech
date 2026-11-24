package com.rentech.app.ui.jelajah

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.Barang
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.HargaPerHari
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Error
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextTertiary

/** Kartu barang di beranda dan hasil pencarian. */
@Composable
fun KartuBarang(barang: Barang, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var disimpan by rememberSaveable(barang.id) { mutableStateOf(false) }
    Column(modifier.kartu().clickable(onClick = onClick)) {
        Box(Modifier.fillMaxWidth().height(110.dp)) {
            Image(
                painterResource(barang.foto),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(110.dp),
            )
            Box(Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                TombolBulat(
                    ikon = R.drawable.ikon_favorite,
                    onClick = { disimpan = !disimpan },
                    ukuran = 34.dp,
                    ukuranIkon = 18.dp,
                    bergaris = false,
                    warnaIkon = if (disimpan) Error else TextPrimary,
                )
            }
        }
        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                barang.nama,
                style = Teks.IsiTebal,
                color = TextPrimary,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            HargaPerHari(rupiah(barang.hargaPerHari))
            Row(
                Modifier.padding(top = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ikon(R.drawable.ikon_verified_fill, Success, ukuran = 14.dp)
                Text("Terverifikasi", style = Teks.Keterangan, color = Success)
                Text("·", style = Teks.Keterangan, color = TextTertiary)
                Text(barang.jarak, style = Teks.Keterangan, color = TextTertiary)
            }
        }
    }
}
