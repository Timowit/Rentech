package com.rentech.server.db

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.date
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

// Cerminan V1__skema_awal.sql. Skema diatur Flyway, bukan Exposed.

object Pengguna : IntIdTable("pengguna") {
    val nama = text("nama")
    val email = text("email")
    val noHp = text("no_hp")
    val sandiHash = text("sandi_hash")
    val foto = text("foto").nullable()
    val peranAktif = text("peran_aktif").default("penyewa")
    val admin = bool("admin").default(false)
    val statusVerifikasi = text("status_verifikasi").default("belum")
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object Verifikasi : IntIdTable("verifikasi") {
    val pengguna = reference("pengguna_id", Pengguna, onDelete = ReferenceOption.CASCADE)
    val fotoKtp = text("foto_ktp")
    val fotoSwafoto = text("foto_swafoto")
    val nik = text("nik")
    val status = text("status").default("diperiksa")
    val alasan = array<String>("alasan").default(emptyList())
    val diperiksaOleh = optReference("diperiksa_oleh", Pengguna)
    val diperiksaPada = timestampWithTimeZone("diperiksa_pada").nullable()
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object Kategori : IntIdTable("kategori") {
    val nama = text("nama")
    val urutan = integer("urutan")
}

object Barang : IntIdTable("barang") {
    val pemilik = reference("pemilik_id", Pengguna)
    val kategori = reference("kategori_id", Kategori)
    val nama = text("nama")
    val hargaPerHari = long("harga_per_hari")
    val deposit = long("deposit")
    val kelengkapan = text("kelengkapan").default("")
    val lokasi = text("lokasi").default("Jakarta Selatan")
    val jarakDemo = text("jarak_demo").default("")
    val tersedia = bool("tersedia").default(true)
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object FotoBarang : IntIdTable("foto_barang") {
    val barang = reference("barang_id", Barang, onDelete = ReferenceOption.CASCADE)
    val path = text("path")
    val urutan = integer("urutan").default(0)
}

object Pesanan : IntIdTable("pesanan") {
    val barang = reference("barang_id", Barang)
    val penyewa = reference("penyewa_id", Pengguna)
    val mulai = date("mulai")
    val selesai = date("selesai")
    val diantar = bool("diantar").default(false)
    val slot = integer("slot").default(0)
    val metode = text("metode")
    val kodeUnik = short("kode_unik").nullable()
    val biayaSewa = long("biaya_sewa")
    val biayaLayanan = long("biaya_layanan")
    val biayaAntar = long("biaya_antar")
    val deposit = long("deposit")
    val total = long("total")
    val status = text("status").default("menunggu_pemilik")
    val statusDana = text("status_dana").default("belum")
    val buktiTransfer = text("bukti_transfer").nullable()
    val alasanBukti = text("alasan_bukti").nullable()
    val diterimaPenyewaPada = timestampWithTimeZone("diterima_penyewa_pada").nullable()
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object Ulasan : IntIdTable("ulasan") {
    val pesanan = reference("pesanan_id", Pesanan)
    val dari = reference("dari_id", Pengguna)
    val untuk = reference("untuk_id", Pengguna)
    val bintang = short("bintang")
    val isi = text("isi").default("")
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object Pesan : IntIdTable("pesan") {
    val barang = reference("barang_id", Barang)
    val pesanan = optReference("pesanan_id", Pesanan)
    val pengirim = reference("pengirim_id", Pengguna)
    val penerima = reference("penerima_id", Pengguna)
    val teks = text("teks")
    val dibaca = bool("dibaca").default(false)
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object TiketBantuan : IntIdTable("tiket_bantuan") {
    val pengguna = reference("pengguna_id", Pengguna)
    val topik = text("topik")
    val status = text("status").default("terbuka")
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object PesanBantuan : IntIdTable("pesan_bantuan") {
    val tiket = reference("tiket_id", TiketBantuan, onDelete = ReferenceOption.CASCADE)
    val dariAdmin = bool("dari_admin")
    val teks = text("teks")
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}

object Notifikasi : IntIdTable("notifikasi") {
    val pengguna = reference("pengguna_id", Pengguna, onDelete = ReferenceOption.CASCADE)
    val jenis = text("jenis")
    val judul = text("judul")
    val isi = text("isi")
    val tujuan = text("tujuan").default("tidak")
    val dibaca = bool("dibaca").default(false)
    val dibuat = timestampWithTimeZone("dibuat").databaseGenerated()
}
