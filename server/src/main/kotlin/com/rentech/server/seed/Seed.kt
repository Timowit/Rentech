package com.rentech.server.seed

import com.rentech.server.Konfigurasi
import com.rentech.server.ZONA_WIB
import com.rentech.server.akun.hashSandi
import com.rentech.server.db.Barang
import com.rentech.server.db.FotoBarang
import com.rentech.server.db.Kategori
import com.rentech.server.db.Notifikasi
import com.rentech.server.db.Pengguna
import com.rentech.server.db.Pesan
import com.rentech.server.db.Pesanan
import com.rentech.server.db.Ulasan
import com.rentech.server.db.Verifikasi
import com.rentech.server.db.buatDataSource
import com.rentech.server.db.migrasi
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.pesanan.BIAYA_LAYANAN
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.sql.DataSource

/** Sandi semua akun contoh. Akun admin tidak memakai ini. */
const val SANDI_DEMO = "rentech123"

// Sama dengan kategoriBarang di ContohBarang.kt.
private val KATEGORI = listOf("Kamera", "Laptop", "Proyektor", "Drone", "Audio", "Konsol game", "Gimbal", "Tablet")

private class BarangContoh(val nama: String, val kategori: String, val foto: String, val harga: Long, val deposit: Long, val jarak: String, val kelengkapan: String)

// Sama dengan contohBarang di ContohBarang.kt, urutan menentukan id 1..7.
private val BARANG = listOf(
    BarangContoh("Kamera mirrorless full-frame", "Kamera", "foto_kamera", 350_000, 2_000_000, "1,2 km", "Lensa kit, baterai dua, kartu memori 64 GB"),
    BarangContoh("Laptop kerja 14 inci", "Laptop", "foto_laptop", 75_000, 1_000_000, "2,4 km", "Pengisi daya dan tas laptop"),
    BarangContoh("Proyektor portabel", "Proyektor", "foto_proyektor", 120_000, 1_000_000, "3,1 km", "Kabel HDMI, remote, dan tripod mini"),
    BarangContoh("Drone lipat 4K", "Drone", "foto_drone", 200_000, 3_000_000, "2,8 km", "Remote, tiga baterai, dan tas"),
    BarangContoh("Konsol game + 2 stik", "Konsol game", "foto_konsol", 90_000, 1_500_000, "3,5 km", "Dua stik, kabel HDMI, dan pengisi daya"),
    BarangContoh("Gimbal stabilizer", "Gimbal", "foto_gimbal", 45_000, 500_000, "1,8 km", "Tripod mini dan kabel pengisi daya"),
    BarangContoh("Headphone peredam bising", "Audio", "foto_headphone", 40_000, 500_000, "3,3 km", "Kabel audio dan tas"),
)

/**
 * Isi database dengan data contoh dari desain. Aman dijalankan berulang:
 * admin di-upsert dari ADMIN_EMAIL, data contoh hanya dimasukkan kalau tabel barang masih kosong.
 */
fun jalankanSeed(ds: DataSource, k: Konfigurasi, penyimpanan: Penyimpanan) {
    val db = Database.connect(ds)
    transaction(db) {
        if (k.adminEmail != null && k.adminSandi != null) upsertAdmin(k.adminEmail.trim().lowercase(), k.adminSandi)
        if (Barang.selectAll().empty()) isiContoh(penyimpanan)
    }
}

private fun upsertAdmin(email: String, sandi: String) {
    val ada = Pengguna.select(Pengguna.id).where { Pengguna.email eq email }.firstOrNull()
    if (ada == null) {
        Pengguna.insert {
            it[nama] = "Tim Rentech"
            it[Pengguna.email] = email
            it[noHp] = "-"
            it[sandiHash] = hashSandi(sandi)
            it[admin] = true
            it[statusVerifikasi] = "terverifikasi"
        }
    } else {
        Pengguna.update({ Pengguna.email eq email }) { it[admin] = true; it[sandiHash] = hashSandi(sandi) }
    }
}

private fun fotoSeed(penyimpanan: Penyimpanan, nama: String, privat: Boolean = false): String {
    val ekstensi = if (nama.endsWith(".png")) "png" else "webp"
    val file = if ('.' in nama) nama else "$nama.webp"
    val bytes = object {}.javaClass.getResourceAsStream("/seed/foto/$file")?.use { it.readBytes() }
        ?: error("Foto seed $file tidak ada di resources")
    return penyimpanan.simpanTerpercaya(bytes, ekstensi, privat)
}

private fun akun(email: String, nama: String, foto: String?, status: String) = Pengguna.insertAndGetId {
    it[Pengguna.nama] = nama
    it[Pengguna.email] = email
    it[noHp] = "0812 0000 0000"
    it[sandiHash] = hashSandi(SANDI_DEMO)
    it[Pengguna.foto] = foto
    it[statusVerifikasi] = status
}.value

private fun wib(tanggal: LocalDate, jam: Int, menit: Int = 0): OffsetDateTime =
    ZonedDateTime.of(tanggal, LocalTime.of(jam, menit), ZONA_WIB).toOffsetDateTime()

private fun isiContoh(penyimpanan: Penyimpanan) {
    // Akun contoh. Desain tidak memakai nama orang sungguhan, jadi namanya juga umum.
    val penyewa = akun("penyewa@rentech.test", "Penyewa contoh", fotoSeed(penyimpanan, "avatar_1"), "terverifikasi")
    val pemilik = akun("pemilik@rentech.test", "Pemilik contoh", fotoSeed(penyimpanan, "avatar_2"), "terverifikasi")
    Pengguna.update({ Pengguna.id eq pemilik }) { it[peranAktif] = "pemilik" }
    val penyewa2 = akun("penyewa2@rentech.test", "Penyewa contoh 2", fotoSeed(penyimpanan, "avatar_3"), "terverifikasi")
    val penyewa3 = akun("penyewa3@rentech.test", "Penyewa contoh 3", fotoSeed(penyimpanan, "avatar_4"), "terverifikasi")
    val diperiksa = akun("diperiksa@rentech.test", "Akun contoh diperiksa", null, "diperiksa")
    val ditolak = akun("ditolak@rentech.test", "Akun contoh ditolak", null, "ditolak")

    Verifikasi.insert {
        it[pengguna] = diperiksa
        it[fotoKtp] = fotoSeed(penyimpanan, "ktp_contoh.png", privat = true)
        it[fotoSwafoto] = fotoSeed(penyimpanan, "swafoto_contoh.png", privat = true)
        it[nik] = "0000000000000000"
    }
    Verifikasi.insert {
        it[pengguna] = ditolak
        it[fotoKtp] = fotoSeed(penyimpanan, "ktp_contoh.png", privat = true)
        it[fotoSwafoto] = fotoSeed(penyimpanan, "swafoto_contoh.png", privat = true)
        it[nik] = "0000000000000000"
        it[status] = "ditolak"
        // Sama dengan alasan di layar VerifikasiDitolak (RentechNavHost.kt).
        it[alasan] = listOf("Foto KTP buram, nomor NIK tidak terbaca", "Wajah pada swafoto tertutup bayangan")
    }

    val kategoriId = KATEGORI.mapIndexed { i, nama -> nama to Kategori.insertAndGetId { it[Kategori.nama] = nama; it[urutan] = i }.value }.toMap()
    val barangId = BARANG.map { b ->
        val id = Barang.insertAndGetId {
            it[Barang.pemilik] = pemilik
            it[kategori] = kategoriId.getValue(b.kategori)
            it[nama] = b.nama
            it[hargaPerHari] = b.harga
            it[deposit] = b.deposit
            it[kelengkapan] = b.kelengkapan
            it[jarakDemo] = b.jarak
        }.value
        FotoBarang.insert { it[barang] = id; it[path] = fotoSeed(penyimpanan, b.foto) }
        id
    }
    val kamera = barangId[0]
    val proyektor = barangId[2]

    fun pesanan(barang: Int, oleh: Int, mulai: LocalDate, selesai: LocalDate, metode: String, status: String, dana: String): Int {
        val b = BARANG[barangId.indexOf(barang)]
        val hari = ChronoUnit.DAYS.between(mulai, selesai)
        return Pesanan.insertAndGetId {
            it[Pesanan.barang] = barang
            it[Pesanan.penyewa] = oleh
            it[Pesanan.mulai] = mulai
            it[Pesanan.selesai] = selesai
            it[Pesanan.metode] = metode
            it[biayaSewa] = hari * b.harga
            it[biayaLayanan] = BIAYA_LAYANAN
            it[biayaAntar] = 0
            it[deposit] = b.deposit
            it[total] = hari * b.harga + BIAYA_LAYANAN + b.deposit
            it[Pesanan.status] = status
            it[statusDana] = dana
        }.value
    }

    // Sewa kamera penyewa contoh: sudah diterima pemilik, transfer sudah diperiksa (layar Sewa saya dan notifikasi G01).
    val sewaKamera = pesanan(kamera, penyewa, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 15), "transfer", "diterima_pemilik", "ditahan")
    // Riwayat untuk layar ulasan (contohRiwayat): proyektor 21–22 Nov, sudah selesai.
    pesanan(proyektor, penyewa, LocalDate.of(2026, 11, 21), LocalDate.of(2026, 11, 22), "tunai", "selesai", "tunai")
    // Permintaan masuk untuk pemilik (contohPermintaan).
    pesanan(kamera, penyewa2, LocalDate.of(2027, 1, 9), LocalDate.of(2027, 1, 12), "transfer", "menunggu_pemilik", "belum")
    pesanan(proyektor, penyewa3, LocalDate.of(2026, 12, 19), LocalDate.of(2026, 12, 20), "transfer", "diterima_pemilik", "belum")
    // Ulasan yang tampil di profil pemilik (G03).
    val sewaLalu = pesanan(kamera, penyewa2, LocalDate.of(2026, 11, 7), LocalDate.of(2026, 11, 9), "tunai", "selesai", "tunai")
    Ulasan.insert {
        it[pesanan] = sewaLalu
        it[dari] = penyewa2
        it[untuk] = pemilik
        it[bintang] = 5.toShort()
        it[isi] = "Kamera bersih dan sesuai foto. Serah terima di Kemang tepat waktu."
    }

    // Percakapan kamera (percakapanSewa di ChatPemilikScreen.kt).
    val hariIni = LocalDate.now(ZONA_WIB)
    listOf(
        Triple(pemilik, "Halo, pesanan kameranya sudah saya terima. Mau diambil jam berapa?", 9 to 20),
        Triple(penyewa, "Jam 10 pagi bisa? Saya ambil sendiri ke Kemang.", 9 to 24),
        Triple(pemilik, "Bisa. Kameranya sudah saya cas penuh dan kartu memorinya sudah dikosongkan.", 9 to 31),
        Triple(penyewa, "Siap, terima kasih!", 9 to 32),
    ).forEach { (dari, teks, waktu) ->
        Pesan.insert {
            it[barang] = kamera
            it[pesanan] = sewaKamera
            it[pengirim] = dari
            it[penerima] = if (dari == penyewa) pemilik else penyewa
            it[Pesan.teks] = teks
            it[dibaca] = true
            it[dibuat] = wib(hariIni, waktu.first, waktu.second)
        }
    }

    // Notifikasi penyewa contoh (contohNotifikasi di NotifikasiScreen.kt). Dua teratas belum dibaca.
    val sekarang = OffsetDateTime.now()
    listOf(
        listOf("diterima", "Pemilik menerima pesananmu", "Kamera mirrorless full-frame · Sab, 12 – Sel, 15 Des 2026", "sewa_saya") to sekarang.minusMinutes(10),
        listOf("dana", "Bukti transfer sudah diperiksa", "Rp3.075.000 ditahan Rentech sampai barang kamu terima.", "sewa_saya") to sekarang.minusHours(1),
        listOf("pesan", "Pesan baru dari pemilik", "“Kameranya sudah saya cas penuh, ambil jam 10 ya.”", "chat") to sekarang.minusHours(2),
        listOf("pengingat", "Waktunya mengembalikan barang", "Proyektor portabel dikembalikan hari ini sebelum 19.00.", "sewa_saya") to wib(LocalDate.of(2026, 11, 22), 8),
        listOf("ulasan", "Beri ulasan untuk sewamu", "Ulasan membantu penyewa lain memilih dengan lebih yakin.", "ulasan") to wib(LocalDate.of(2026, 11, 22), 20),
        listOf("terverifikasi", "Identitas kamu terverifikasi", "Sekarang kamu bisa menyewa dan menyewakan barang.", "tidak") to wib(LocalDate.of(2026, 11, 16), 9),
    ).forEachIndexed { i, (n, waktu) ->
        Notifikasi.insert {
            it[pengguna] = penyewa
            it[jenis] = n[0]
            it[judul] = n[1]
            it[isi] = n[2]
            it[tujuan] = n[3]
            it[dibaca] = i >= 2
            it[dibuat] = waktu
        }
    }
}

/** ./gradlew seed: migrasi lalu seed, memakai environment yang sama dengan server. */
fun main() {
    val k = Konfigurasi.dariEnv()
    buatDataSource(k).use { ds ->
        migrasi(ds)
        jalankanSeed(ds, k, Penyimpanan(k.fotoDir))
    }
}
