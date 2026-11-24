package com.rentech.app.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.login.AturUlangSandiScreen
import com.rentech.app.ui.login.DaftarScreen
import com.rentech.app.ui.login.LupaSandiScreen
import com.rentech.app.ui.login.MasukScreen
import com.rentech.app.ui.login.OtpScreen
import com.rentech.app.ui.pemilik.BerandaPemilikScreen
import com.rentech.app.ui.pemilik.HargaKetersediaanScreen
import com.rentech.app.ui.pemilik.PermintaanScreen
import com.rentech.app.ui.pemilik.TambahBarangScreen
import com.rentech.app.ui.onboarding.OnboardingScreen
import com.rentech.app.ui.onboarding.Peran
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
    const val BERANDA_PEMILIK = "beranda_pemilik"
    const val TAMBAH_BARANG = "tambah_barang"
    const val HARGA_BARANG = "harga_barang"
    const val PERMINTAAN = "permintaan"
}

@Composable
fun RentechNavHost() {
    val nav = rememberNavController()
    var peran by rememberSaveable { mutableStateOf(Peran.Penyewa) }

    NavHost(navController = nav, startDestination = Rute.SPLASH) {
        composable(Rute.SPLASH) {
            SplashScreen(onSelesai = {
                nav.navigate(Rute.ONBOARDING) { popUpTo(Rute.SPLASH) { inclusive = true } }
            })
        }
        composable(Rute.ONBOARDING) {
            OnboardingScreen(onSelesai = { dipilih ->
                peran = dipilih
                nav.navigate(Rute.MASUK) { popUpTo(Rute.ONBOARDING) { inclusive = true } }
            })
        }
        composable(Rute.MASUK) {
            MasukScreen(
                onMasuk = {
                    if (peran == Peran.Pemilik) {
                        nav.navigate(Rute.BERANDA_PEMILIK) { popUpTo(Rute.MASUK) { inclusive = true } }
                    }
                },
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
        composable(Rute.BERANDA_PEMILIK) {
            BerandaPemilikScreen(
                onTambahBarang = { nav.navigate(Rute.TAMBAH_BARANG) },
                onLihatPermintaan = { nav.bukaTabPemilik(2) },
                onPilihTab = { nav.bukaTabPemilik(it) },
            )
        }
        composable(Rute.TAMBAH_BARANG) {
            TambahBarangScreen(
                onKembali = { nav.popBackStack() },
                onLanjut = { nav.navigate(Rute.HARGA_BARANG) },
            )
        }
        composable(Rute.HARGA_BARANG) {
            HargaKetersediaanScreen(
                onKembali = { nav.popBackStack() },
                onTerbitkan = { nav.popBackStack(Rute.BERANDA_PEMILIK, inclusive = false) },
            )
        }
        composable(Rute.PERMINTAAN) {
            PermintaanScreen(onPilihTab = { nav.bukaTabPemilik(it) })
        }
    }
}

/** Pindah antar tab di navigasi bawah mode pemilik tanpa menumpuk riwayat. */
private fun NavHostController.bukaTabPemilik(indeks: Int) {
    val tujuan = when (indeks) {
        0 -> Rute.BERANDA_PEMILIK
        2 -> Rute.PERMINTAAN
        else -> return
    }
    navigate(tujuan) {
        popUpTo(Rute.BERANDA_PEMILIK) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
