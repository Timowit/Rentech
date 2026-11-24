package com.rentech.app.navigasi

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.login.AturUlangSandiScreen
import com.rentech.app.ui.login.DaftarScreen
import com.rentech.app.ui.login.LupaSandiScreen
import com.rentech.app.ui.login.MasukScreen
import com.rentech.app.ui.login.OtpScreen
import com.rentech.app.ui.pemilik.BerandaPemilikScreen
import com.rentech.app.ui.pemilik.HargaKetersediaanScreen
import com.rentech.app.ui.pemilik.PermintaanScreen
import com.rentech.app.data.cariBarang
import com.rentech.app.ui.jelajah.BerandaScreen
import com.rentech.app.ui.jelajah.DaftarTungguScreen
import com.rentech.app.ui.jelajah.DetailBarangScreen
import com.rentech.app.ui.jelajah.PerlindunganScreen
import com.rentech.app.ui.jelajah.HasilPencarianScreen
import com.rentech.app.ui.jelajah.PencarianScreen
import com.rentech.app.ui.pemilik.TambahBarangScreen
import com.rentech.app.ui.profil.BantuanScreen
import com.rentech.app.ui.profil.ProfilScreen
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
    const val BERANDA = "beranda"
    const val PENCARIAN = "pencarian?q={q}"
    const val HASIL = "hasil?q={q}&filter={filter}"

    const val DETAIL = "barang/{id}"
    const val PERLINDUNGAN = "perlindungan"
    const val DAFTAR_TUNGGU = "daftar_tunggu"

    fun detail(id: Int) = "barang/$id"
    fun pencarian(q: String = "") = "pencarian?q=${Uri.encode(q)}"
    fun hasil(q: String, bukaFilter: Boolean = false) = "hasil?q=${Uri.encode(q)}&filter=$bukaFilter"
    const val BERANDA_PEMILIK = "beranda_pemilik"
    const val TAMBAH_BARANG = "tambah_barang"
    const val HARGA_BARANG = "harga_barang"
    const val PERMINTAAN = "permintaan"
    const val PROFIL = "profil"
    const val BANTUAN = "bantuan"
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
                onMasuk = { nav.mulaiSebagai(peran) },
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
            VerifikasiDiperiksaScreen(onJelajahi = { nav.mulaiSebagai(peran) })
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
        composable(Rute.BERANDA) {
            BerandaScreen(
                onCari = { nav.navigate(Rute.pencarian()) },
                onFilter = { nav.navigate(Rute.hasil("", bukaFilter = true)) },
                onCaraKerja = { nav.navigate(Rute.PERLINDUNGAN) },
                onKategori = { nav.navigate(Rute.hasil(it)) },
                onBarang = { nav.navigate(Rute.detail(it.id)) },
                onLihatSemua = { nav.navigate(Rute.hasil("")) },
                onPilihTab = { nav.bukaTabPenyewa(it) },
            )
        }
        composable(
            Rute.PENCARIAN,
            arguments = listOf(navArgument("q") { type = NavType.StringType; defaultValue = "" }),
        ) { entri ->
            PencarianScreen(
                kueriAwal = entri.arguments?.getString("q").orEmpty(),
                onKembali = { nav.popBackStack() },
                onCari = { nav.navigate(Rute.hasil(it)) },
                onKategori = { nav.navigate(Rute.hasil(it)) },
            )
        }
        composable(
            Rute.HASIL,
            arguments = listOf(
                navArgument("q") { type = NavType.StringType; defaultValue = "" },
                navArgument("filter") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { entri ->
            val kueri = entri.arguments?.getString("q").orEmpty()
            HasilPencarianScreen(
                kueri = kueri,
                bukaFilter = entri.arguments?.getBoolean("filter") ?: false,
                onKembali = { nav.popBackStack() },
                onUbahKueri = {
                    nav.popBackStack()
                    nav.navigate(Rute.pencarian(kueri)) { launchSingleTop = true }
                },
                onBarang = { nav.navigate(Rute.detail(it.id)) },
            )
        }
        composable(Rute.DETAIL, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entri ->
            DetailBarangScreen(
                barang = cariBarang(entri.arguments?.getInt("id") ?: 1),
                onKembali = { nav.popBackStack() },
                onPemilik = { },
                onPerlindungan = { nav.navigate(Rute.PERLINDUNGAN) },
                onSewa = { },
            )
        }
        composable(Rute.PERLINDUNGAN) {
            PerlindunganScreen(
                onKembali = { nav.popBackStack() },
                onDaftarTunggu = { nav.navigate(Rute.DAFTAR_TUNGGU) },
            )
        }
        composable(Rute.DAFTAR_TUNGGU) { DaftarTungguScreen() }
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
        composable(Rute.PROFIL) {
            ProfilScreen(
                peran = peran,
                onGantiPeran = { baru ->
                    peran = baru
                    nav.mulaiSebagai(baru)
                },
                onBantuan = { nav.navigate(Rute.BANTUAN) },
                onKeluar = {
                    nav.navigate(Rute.MASUK) { popUpTo(0) { inclusive = true } }
                },
                onPilihTab = { if (peran == Peran.Pemilik) nav.bukaTabPemilik(it) else nav.bukaTabPenyewa(it) },
            )
        }
        composable(Rute.BANTUAN) {
            BantuanScreen(
                onKembali = { nav.popBackStack() },
                onMulaiPercakapan = { },
                onPilihTab = { if (it == 3) nav.popBackStack() else nav.bukaTabPenyewa(it) },
            )
        }
    }
}

/** Buka beranda sesuai peran dan buang riwayat sebelumnya (masuk, verifikasi, atau peran lama). */
private fun NavHostController.mulaiSebagai(peran: Peran) {
    val beranda = if (peran == Peran.Pemilik) Rute.BERANDA_PEMILIK else Rute.BERANDA
    navigate(beranda) { popUpTo(0) { inclusive = true } }
}

/** Pindah antar tab di navigasi bawah mode penyewa tanpa menumpuk riwayat. */
private fun NavHostController.bukaTabPenyewa(indeks: Int) {
    val tujuan = when (indeks) {
        0 -> Rute.BERANDA
        1 -> Rute.pencarian()
        3 -> Rute.PROFIL
        else -> return
    }
    navigate(tujuan) {
        popUpTo(Rute.BERANDA) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Pindah antar tab di navigasi bawah mode pemilik tanpa menumpuk riwayat. */
private fun NavHostController.bukaTabPemilik(indeks: Int) {
    val tujuan = when (indeks) {
        0 -> Rute.BERANDA_PEMILIK
        2 -> Rute.PERMINTAAN
        3 -> Rute.PROFIL
        else -> return
    }
    navigate(tujuan) {
        popUpTo(Rute.BERANDA_PEMILIK) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
