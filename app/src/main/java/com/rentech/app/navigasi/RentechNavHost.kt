package com.rentech.app.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.login.AturUlangSandiScreen
import com.rentech.app.ui.login.DaftarScreen
import com.rentech.app.ui.login.LupaSandiScreen
import com.rentech.app.ui.login.MasukScreen
import com.rentech.app.ui.login.OtpScreen
import com.rentech.app.ui.onboarding.OnboardingScreen
import com.rentech.app.ui.onboarding.SplashScreen
import com.rentech.app.ui.verifikasi.VerifikasiDiperiksaScreen
import com.rentech.app.ui.verifikasi.VerifikasiDitolakScreen
import com.rentech.app.ui.verifikasi.VerifikasiIdentitasScreen

object Rute {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MASUK = "masuk"
    const val DAFTAR = "daftar"
    const val OTP = "otp"
    const val LUPA_SANDI = "lupa_sandi"
    const val ATUR_ULANG_SANDI = "atur_ulang_sandi"
    const val VERIFIKASI_IDENTITAS = "verifikasi_identitas"
    const val VERIFIKASI_DIPERIKSA = "verifikasi_diperiksa"
    const val VERIFIKASI_DITOLAK = "verifikasi_ditolak"
}

@Composable
fun RentechNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rute.SPLASH) {
        composable(Rute.SPLASH) {
            SplashScreen(onSelesai = {
                nav.navigate(Rute.ONBOARDING) { popUpTo(Rute.SPLASH) { inclusive = true } }
            })
        }
        composable(Rute.ONBOARDING) {
            OnboardingScreen(onSelesai = {
                nav.navigate(Rute.MASUK) { popUpTo(Rute.ONBOARDING) { inclusive = true } }
            })
        }
        composable(Rute.MASUK) {
            MasukScreen(
                onMasuk = { },
                onLupaSandi = { nav.navigate(Rute.LUPA_SANDI) },
                onDaftar = { nav.navigate(Rute.DAFTAR) },
            )
        }
        composable(Rute.DAFTAR) {
            DaftarScreen(
                onKembali = { nav.popBackStack() },
                onBuatAkun = { nav.navigate(Rute.OTP) },
                onMasuk = { nav.popBackStack(Rute.MASUK, inclusive = false) },
            )
        }
        composable(Rute.OTP) {
            OtpScreen(
                nomorHp = "0812 •••• 7890",
                onKembali = { nav.popBackStack() },
                onVerifikasi = { nav.navigate(Rute.VERIFIKASI_IDENTITAS) },
                onGantiNomor = { nav.popBackStack() },
            )
        }
        composable(Rute.LUPA_SANDI) {
            LupaSandiScreen(
                onKembali = { nav.popBackStack() },
                onKirimTautan = { nav.navigate(Rute.ATUR_ULANG_SANDI) },
            )
        }
        composable(Rute.ATUR_ULANG_SANDI) {
            AturUlangSandiScreen(
                onKembali = { nav.popBackStack() },
                onSimpan = { nav.popBackStack(Rute.MASUK, inclusive = false) },
            )
        }
        composable(Rute.VERIFIKASI_IDENTITAS) {
            VerifikasiIdentitasScreen(
                onKembali = { nav.popBackStack() },
                onKirim = {
                    nav.navigate(Rute.VERIFIKASI_DIPERIKSA) {
                        popUpTo(Rute.MASUK) { inclusive = true }
                    }
                },
            )
        }
        composable(Rute.VERIFIKASI_DIPERIKSA) {
            VerifikasiDiperiksaScreen(onJelajahi = { })
        }
        composable(Rute.VERIFIKASI_DITOLAK) {
            VerifikasiDitolakScreen(
                alasan = listOf(
                    "Foto KTP buram, nomor NIK tidak terbaca",
                    "Wajah pada swafoto tertutup bayangan",
                ),
                onKirimUlang = { nav.navigate(Rute.VERIFIKASI_IDENTITAS) },
                onHubungiTim = { },
            )
        }
    }
}
