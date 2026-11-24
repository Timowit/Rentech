package com.rentech.app.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class Peran { Penyewa, Pemilik }

private data class Halaman(@DrawableRes val gambar: Int, val judul: String, val isi: String)

private val halamanPerkenalan = listOf(
    Halaman(
        R.drawable.onboarding_1,
        "Butuh kamera seminggu?\nSewa saja.",
        "Sewa perangkat elektronik dari orang terdekat untuk kebutuhan singkat, tanpa harus membeli.",
    ),
    Halaman(
        R.drawable.onboarding_2,
        "Dana kamu aman sampai barang diterima.",
        "Pembayaran ditahan dan baru diteruskan ke pemilik setelah kamu menerima barang.",
    ),
)

@Composable
fun OnboardingScreen(onSelesai: (Peran) -> Unit) {
    val pager = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()
    var peran by rememberSaveable { mutableStateOf(Peran.Penyewa) }
    val halamanTerakhir = pager.currentPage == 2

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 21.dp, end = 21.dp, top = 21.dp, bottom = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(R.drawable.logo_rentech),
                contentDescription = "Rentech",
                modifier = Modifier.width(90.dp),
            )
            Text(
                "Lewati",
                style = Teks.IsiTebal,
                color = TextSecondary,
                modifier = Modifier.clickable { onSelesai(peran) },
            )
        }

        HorizontalPager(pager, Modifier.weight(1f)) { i ->
            if (i < 2) HalamanPerkenalan(halamanPerkenalan[i])
            else HalamanPilihPeran(peran, onPilih = { peran = it })
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 21.dp, end = 21.dp, bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Indikator(jumlah = 3, aktif = pager.currentPage)
            Tombol(
                teks = if (halamanTerakhir) "Mulai" else "Lanjut",
                onClick = {
                    if (halamanTerakhir) onSelesai(peran)
                    else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HalamanPerkenalan(h: Halaman) {
    Column(Modifier.fillMaxSize()) {
        Image(
            painterResource(h.gambar),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(470.dp),
        )
        Column(
            Modifier.padding(start = 21.dp, end = 21.dp, top = 34.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Text(h.judul, style = Teks.Display, color = TextPrimary)
            Text(h.isi, style = Teks.Isi, color = TextSecondary)
        }
    }
}

@Composable
private fun HalamanPilihPeran(peran: Peran, onPilih: (Peran) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Image(
            painterResource(R.drawable.onboarding_3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .height(290.dp),
        )
        Column(
            Modifier.padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pilih peranmu di Rentech", style = Teks.Judul, color = TextPrimary)
                Text(
                    "Satu akun bisa dipakai untuk menyewa dan menyewakan. Kamu bisa ganti peran kapan saja.",
                    style = Teks.Isi,
                    color = TextSecondary,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                PilihanPeran(
                    ikon = R.drawable.ikon_search,
                    judul = "Saya ingin menyewa",
                    isi = "Cari perangkat untuk kebutuhan sementara, dari kamera sampai proyektor.",
                    terpilih = peran == Peran.Penyewa,
                    onClick = { onPilih(Peran.Penyewa) },
                )
                PilihanPeran(
                    ikon = R.drawable.ikon_storefront,
                    judul = "Saya ingin menyewakan",
                    isi = "Sewakan perangkat yang sedang menganggur di sekitar kamu.",
                    terpilih = peran == Peran.Pemilik,
                    onClick = { onPilih(Peran.Pemilik) },
                )
            }
        }
    }
}

@Composable
private fun PilihanPeran(
    @DrawableRes ikon: Int,
    judul: String,
    isi: String,
    terpilih: Boolean,
    onClick: () -> Unit,
) {
    val bentuk = RoundedCornerShape(21.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(bentuk)
            .background(if (terpilih) PrimaryContainer else Surface)
            .border(if (terpilih) 2.dp else 1.dp, if (terpilih) Primary else Outline, bentuk)
            .clickable(onClick = onClick)
            .padding(18.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WadahIkon(
            ikon,
            warnaIkon = if (terpilih) OnPrimary else TextPrimary,
            latar = if (terpilih) Primary else SurfaceVariant,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(isi, style = Teks.Keterangan, color = TextSecondary)
        }
        Ikon(
            if (terpilih) R.drawable.ikon_check_circle_fill else R.drawable.ikon_check_circle,
            if (terpilih) Primary else Outline,
            ukuran = 22.dp,
        )
    }
}

@Composable
private fun Indikator(jumlah: Int, aktif: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(jumlah) { i ->
            val lebar by animateDpAsState(if (i == aktif) 21.dp else 8.dp, label = "lebar")
            val warna by animateColorAsState(if (i == aktif) Primary else Outline, label = "warna")
            Box(
                Modifier
                    .size(width = lebar, height = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(warna)
            )
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun OnboardingPreview() {
    RentechTheme { OnboardingScreen(onSelesai = {}) }
}
