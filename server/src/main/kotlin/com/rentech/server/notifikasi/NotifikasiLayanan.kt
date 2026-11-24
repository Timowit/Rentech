package com.rentech.server.notifikasi

import com.rentech.server.db.Notifikasi
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/** Jenis sama dengan enum Jenis di NotifikasiScreen.kt, ditambah "ditolak". */
val JENIS_NOTIFIKASI = setOf("diterima", "dana", "pesan", "pengingat", "ulasan", "terverifikasi", "ditolak")

/** Tujuan sama dengan TujuanNotifikasi di aplikasi. */
val TUJUAN_NOTIFIKASI = setOf("sewa_saya", "chat", "ulasan", "tidak")

class NotifikasiLayanan {
    /** Ikut transaksi pemanggil kalau ada, supaya notifikasi tersimpan bersama perubahannya. */
    fun kirim(penggunaId: Int, jenis: String, judul: String, isi: String, tujuan: String = "tidak") {
        require(jenis in JENIS_NOTIFIKASI) { "Jenis notifikasi tidak dikenal: $jenis" }
        require(tujuan in TUJUAN_NOTIFIKASI) { "Tujuan notifikasi tidak dikenal: $tujuan" }
        transaction {
            Notifikasi.insert {
                it[pengguna] = penggunaId
                it[Notifikasi.jenis] = jenis
                it[Notifikasi.judul] = judul
                it[Notifikasi.isi] = isi
                it[Notifikasi.tujuan] = tujuan
            }
        }
    }
}
