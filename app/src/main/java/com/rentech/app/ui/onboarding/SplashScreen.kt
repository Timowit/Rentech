package com.rentech.app.ui.onboarding

import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rentech.app.R
import com.rentech.app.ui.theme.Inter
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import kotlinx.coroutines.delay

// Timeline animasi "Logo animasi" di frame A01 Splash Figma, dalam milidetik.
private const val DURASI_ANIMASI = 4000
private const val PINDAH_SETELAH = 4300L

private val MasukLembut = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
private val PutarSeimbang = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val GeserTegas = CubicBezierEasing(0.2f, 0f, 0f, 1f)

/** Nilai dari [dari] ke [ke] selama [mulai]..[selesai] ms pada waktu [t]; di luar rentang itu nilainya diam. */
private fun segmen(t: Float, mulai: Int, selesai: Int, dari: Float, ke: Float, easing: Easing): Float {
    val p = ((t - mulai) / (selesai - mulai)).coerceIn(0f, 1f)
    return dari + (ke - dari) * easing.transform(p)
}

@Composable
fun SplashScreen(onSelesai: () -> Unit) {
    val konteks = LocalContext.current
    // Animasi dimatikan di pengaturan perangkat: langsung tampilkan keadaan akhir.
    val tanpaAnimasi = remember {
        Settings.Global.getFloat(konteks.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val waktu = remember { Animatable(if (tanpaAnimasi) DURASI_ANIMASI.toFloat() else 0f) }

    LaunchedEffect(Unit) {
        if (tanpaAnimasi) {
            delay(1000)
        } else {
            waktu.animateTo(DURASI_ANIMASI.toFloat(), tween(DURASI_ANIMASI, easing = LinearEasing))
            delay(PINDAH_SETELAH - DURASI_ANIMASI)
        }
        onSelesai()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Primary)
            .semantics(mergeDescendants = true) { contentDescription = "Rentech" },
        verticalArrangement = Arrangement.spacedBy(21.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LogoAnimasi { waktu.value }
        Text(
            "Sewa perangkat, tanpa perlu beli",
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 15.sp),
            color = OnPrimary,
            modifier = Modifier.graphicsLayer {
                val t = waktu.value
                alpha = segmen(t, 3000, 3500, 0f, 0.85f, MasukLembut)
                translationY = segmen(t, 3000, 3500, 8f, 0f, MasukLembut).dp.toPx()
            },
        )
    }
}

/**
 * Dua huruf r bertemu, simbolnya berputar setengah putaran, lalu bergeser ke kiri
 * sementara tulisan "rentech" muncul dari baliknya. Ukuran dan posisi sama dengan frame 300x96 di Figma.
 * [waktu] dibaca di dalam graphicsLayer supaya animasi tidak memicu recompose.
 */
@Composable
private fun LogoAnimasi(waktu: () -> Float) {
    Box(Modifier.size(300.dp, 96.dp)) {
        Box(
            Modifier
                .offset(9.dp, 4.dp)
                .size(88.dp)
                .graphicsLayer {
                    val t = waktu()
                    rotationZ = segmen(t, 950, 2150, 0f, 180f, PutarSeimbang)
                    translationX = segmen(t, 2250, 3050, 97f, 0f, GeserTegas).dp.toPx()
                },
        ) {
            HurufR(R.drawable.splash_r_kiri, waktu, dariX = -56f, dariY = 56f)
            HurufR(R.drawable.splash_r_kanan, waktu, dariX = 56f, dariY = -56f)
        }
        // Wadah memotong isinya, jadi tulisan tersembunyi di balik simbol sebelum bergeser masuk.
        Box(
            Modifier
                .offset(111.dp, 24.dp)
                .size(190.dp, 48.dp)
                .clipToBounds()
                .graphicsLayer { translationX = segmen(waktu(), 2250, 3050, 97f, 0f, GeserTegas).dp.toPx() },
        ) {
            Image(
                painterResource(R.drawable.splash_wordmark),
                contentDescription = null,
                modifier = Modifier
                    .offset(0.dp, 4.dp)
                    .graphicsLayer {
                        val t = waktu()
                        alpha = segmen(t, 2300, 2750, 0f, 1f, GeserTegas)
                        translationX = segmen(t, 2300, 3100, -190f, 0f, GeserTegas).dp.toPx()
                    },
            )
        }
    }
}

@Composable
private fun HurufR(@DrawableRes gambar: Int, waktu: () -> Float, dariX: Float, dariY: Float) {
    Image(
        painterResource(gambar),
        contentDescription = null,
        modifier = Modifier.graphicsLayer {
            val t = waktu()
            alpha = segmen(t, 150, 500, 0f, 1f, MasukLembut)
            translationX = segmen(t, 150, 850, dariX, 0f, MasukLembut).dp.toPx()
            translationY = segmen(t, 150, 850, dariY, 0f, MasukLembut).dp.toPx()
        },
    )
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun SplashPreview() {
    RentechTheme { SplashScreen(onSelesai = {}) }
}
