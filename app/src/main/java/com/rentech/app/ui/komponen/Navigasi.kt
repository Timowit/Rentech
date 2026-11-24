package com.rentech.app.ui.komponen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

/** Bilah atas dengan tombol kembali bulat dan judul halaman. */
@Composable
fun BilahAtas(judul: String, onKembali: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 21.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .kartu(radius = 20.dp)
                .clickable(onClick = onKembali),
            contentAlignment = Alignment.Center,
        ) {
            Ikon(R.drawable.ikon_arrow_back, TextPrimary, ukuran = 22.dp)
        }
        Text(judul, style = Teks.JudulBilah, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

data class TabNavigasi(
    val label: String,
    @DrawableRes val ikon: Int,
    @DrawableRes val ikonAktif: Int,
)

val TabPenyewa = listOf(
    TabNavigasi("Beranda", R.drawable.ikon_home, R.drawable.ikon_home_fill),
    TabNavigasi("Cari", R.drawable.ikon_search, R.drawable.ikon_search),
    TabNavigasi("Sewa saya", R.drawable.ikon_calendar_month, R.drawable.ikon_calendar_month),
    TabNavigasi("Profil", R.drawable.ikon_person, R.drawable.ikon_person_fill),
)

val TabPemilik = listOf(
    TabNavigasi("Beranda", R.drawable.ikon_home, R.drawable.ikon_home_fill),
    TabNavigasi("Barang", R.drawable.ikon_inventory_2, R.drawable.ikon_inventory_2),
    TabNavigasi("Permintaan", R.drawable.ikon_mail, R.drawable.ikon_mail_fill),
    TabNavigasi("Profil", R.drawable.ikon_person, R.drawable.ikon_person_fill),
)

@Composable
fun NavigasiBawah(
    tab: List<TabNavigasi>,
    terpilih: Int,
    onPilih: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Surface)
            .drawBehind { drawLine(Outline, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .navigationBarsPadding()
            .padding(start = 13.dp, end = 13.dp, top = 13.dp, bottom = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tab.forEachIndexed { i, t ->
            val aktif = i == terpilih
            Column(
                Modifier
                    .width(89.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) { onPilih(i) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(
                    Modifier
                        .width(55.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (aktif) PrimaryContainer else Surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Ikon(
                        if (aktif) t.ikonAktif else t.ikon,
                        if (aktif) OnPrimaryContainer else TextSecondary,
                        ukuran = 22.dp,
                    )
                }
                Text(
                    t.label,
                    style = Teks.Keterangan.copy(fontWeight = if (aktif) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (aktif) TextPrimary else TextSecondary,
                )
            }
        }
    }
}

/** Pilihan bersegmen, misalnya tab "Baru / Berjalan / Selesai" atau "Penyewa / Pemilik". */
@Composable
fun Segmen(
    pilihan: List<String>,
    terpilih: Int,
    onPilih: (Int) -> Unit,
    modifier: Modifier = Modifier,
    jumlah: List<Int?> = pilihan.map { null },
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceVariant)
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        pilihan.forEachIndexed { i, label ->
            val aktif = i == terpilih
            Row(
                Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(if (aktif) Surface else SurfaceVariant)
                    .clickable { onPilih(i) },
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, style = Teks.IsiTebal, color = if (aktif) TextPrimary else TextSecondary)
                jumlah[i]?.let { n ->
                    Box(
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (aktif) Primary else Outline),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$n", style = Teks.Keterangan, color = if (aktif) Surface else TextSecondary)
                    }
                }
            }
        }
    }
}
