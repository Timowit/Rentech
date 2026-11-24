package com.rentech.app.navigasi

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.chat.ChatPemilikScreen
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
import com.rentech.app.ui.sewa.DraftSewa
import com.rentech.app.ui.sewa.MenungguKonfirmasiScreen
import com.rentech.app.ui.sewa.MetodePembayaranScreen
import com.rentech.app.ui.sewa.PilihTanggalScreen
import com.rentech.app.ui.sewa.RingkasanSewaScreen
import com.rentech.app.ui.sewa.SerahTerimaScreen
import com.rentech.app.ui.sewa.rentangLengkap
import com.rentech.app.ui.sewa.TransferScreen
import com.rentech.app.ui.sewasaya.KonfirmasiTerimaScreen
import com.rentech.app.ui.sewasaya.SewaSayaScreen
import com.rentech.app.ui.sewasaya.UlasanScreen
import com.rentech.app.ui.sewasaya.contohRiwayat
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
    const val SEWA_TANGGAL = "sewa_tanggal"
    const val SEWA_SERAH = "sewa_serah_terima"
    const val SEWA_RINGKASAN = "sewa_ringkasan"
    const val SEWA_METODE = "sewa_metode"
    const val SEWA_TRANSFER = "sewa_transfer"
    const val SEWA_MENUNGGU = "sewa_menunggu"
    const val SEWA_SAYA = "sewa_saya"
    const val KONFIRMASI_TERIMA = "konfirmasi_terima"
    const val ULASAN = "ulasan/{indeks}"
    const val CHAT = "chat/{id}?sewa={sewa}"

    fun chat(id: Int, sewa: Boolean) = "chat/$id?sewa=$sewa"

    fun ulasan(indeks: Int) = "ulasan/$indeks"

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
    val draft = remember { DraftSewa() }

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
            val barang = cariBarang(entri.arguments?.getInt("id") ?: 1)
            DetailBarangScreen(
                barang = barang,
                onKembali = { nav.popBackStack() },
                onPemilik = { },
                onPerlindungan = { nav.navigate(Rute.PERLINDUNGAN) },
                onSewa = {
                    draft.barangId = barang.id
                    nav.navigate(Rute.SEWA_TANGGAL)
                },
            )
        }
        composable(Rute.SEWA_TANGGAL) {
            PilihTanggalScreen(draft, onKembali = { nav.popBackStack() }, onLanjut = { nav.navigate(Rute.SEWA_SERAH) })
        }
        composable(Rute.SEWA_SERAH) {
            SerahTerimaScreen(draft, onKembali = { nav.popBackStack() }, onLanjut = { nav.navigate(Rute.SEWA_RINGKASAN) })
        }
        composable(Rute.SEWA_RINGKASAN) {
            RingkasanSewaScreen(draft, onKembali = { nav.popBackStack() }, onBayar = { nav.navigate(Rute.SEWA_METODE) })
        }
        composable(Rute.SEWA_METODE) {
            MetodePembayaranScreen(
                draft,
                onKembali = { nav.popBackStack() },
                onLanjut = { if (draft.tunai) nav.selesaikanSewa() else nav.navigate(Rute.SEWA_TRANSFER) },
            )
        }
        composable(Rute.SEWA_TRANSFER) {
            TransferScreen(draft, onKembali = { nav.popBackStack() }, onKirim = { nav.selesaikanSewa() })
        }
        composable(Rute.SEWA_MENUNGGU) {
            MenungguKonfirmasiScreen(
                draft,
                onLihatPesanan = { nav.bukaTabPenyewa(2) },
                onBeranda = { nav.bukaTabPenyewa(0) },
            )
        }
        composable(Rute.SEWA_SAYA) {
            SewaSayaScreen(
                draft,
                onKonfirmasi = { nav.navigate(Rute.KONFIRMASI_TERIMA) },
                onChat = { nav.navigate(Rute.chat(draft.barangId, sewa = true)) },
                onUlas = { nav.navigate(Rute.ulasan(contohRiwayat.indexOf(it))) },
                onPilihTab = { nav.bukaTabPenyewa(it) },
            )
        }
        composable(
            Rute.CHAT,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("sewa") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { entri ->
            val barang = cariBarang(entri.arguments?.getInt("id") ?: 1)
            val sewa = entri.arguments?.getBoolean("sewa") ?: false
            ChatPemilikScreen(
                barang = barang,
                keterangan = if (sewa) rentangLengkap(draft) else "${barang.lokasi} · ${barang.jarak}",
                danaDitahan = if (sewa) !draft.tunai else null,
                onKembali = { nav.popBackStack() },
            )
        }
        composable(Rute.KONFIRMASI_TERIMA) {
            KonfirmasiTerimaScreen(
                draft,
                onKembali = { nav.popBackStack() },
                onKonfirmasi = {
                    draft.diterima = true
                    nav.popBackStack()
                },
            )
        }
        composable(Rute.ULASAN, arguments = listOf(navArgument("indeks") { type = NavType.IntType })) { entri ->
            UlasanScreen(
                contohRiwayat[entri.arguments?.getInt("indeks") ?: 0],
                onKembali = { nav.popBackStack() },
                onKirim = { nav.popBackStack() },
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

/** Pesanan terkirim: halaman alur sewa dibuang supaya tombol kembali tidak membuka pembayaran lagi. */
private fun NavHostController.selesaikanSewa() {
    navigate(Rute.SEWA_MENUNGGU) { popUpTo(Rute.BERANDA) }
}

/** Pindah antar tab di navigasi bawah mode penyewa tanpa menumpuk riwayat. */
private fun NavHostController.bukaTabPenyewa(indeks: Int) {
    val tujuan = when (indeks) {
        0 -> Rute.BERANDA
        1 -> Rute.pencarian()
        2 -> Rute.SEWA_SAYA
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
