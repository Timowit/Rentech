package com.rentech.server.seed

import com.rentech.server.UjiDb
import com.rentech.server.barang.BarangDto
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.nilaiDb
import com.rentech.server.ujiAplikasi
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class SeedTest {
    private fun seed(k: com.rentech.server.Konfigurasi) = jalankanSeed(UjiDb.ds, k, Penyimpanan(k.fotoDir))

    @Test
    fun `seed mengisi tujuh barang sesuai contoh aplikasi`() {
        val k = UjiDb.konfigurasi()
        ujiAplikasi(k) { klien ->
            seed(k)
            val barang = klien.get("/barang").body<List<BarangDto>>()
            assertEquals(7, barang.size)
            val kamera = barang.first { it.id == barang.minOf { b -> b.id } }
            assertEquals("Kamera mirrorless full-frame", kamera.nama)
            assertEquals(350_000, kamera.hargaPerHari)
            assertEquals("1,2 km", kamera.jarak)
            assertEquals(HttpStatusCode.OK, klien.get(kamera.foto.first()).status)
            assertEquals(8, klien.get("/kategori").body<List<String>>().size)
        }
    }

    @Test
    fun `seed dua kali tidak menggandakan dan tidak menimpa`() {
        val k = UjiDb.konfigurasi(adminEmail = "admin@rentech.test", adminSandi = "sandi-admin-uji")
        ujiAplikasi(k) { klien ->
            seed(k)
            UjiDb.ds.connection.use { it.createStatement().execute("update barang set nama = 'Diubah' where id = (select min(id) from barang)") }
            seed(k)
            assertEquals("7", nilaiDb("select count(*) from barang"))
            assertEquals("1", nilaiDb("select count(*) from pengguna where admin"))
            assertEquals("Diubah", nilaiDb("select nama from barang order by id limit 1"))
        }
    }

    @Test
    fun `tanpa admin email tidak ada admin`() {
        val k = UjiDb.konfigurasi()
        ujiAplikasi(k) { klien ->
            klien.get("/sehat")
            seed(k)
            assertEquals("0", nilaiDb("select count(*) from pengguna where admin"))
        }
    }

    @Test
    fun `antrean verifikasi dan notifikasi contoh terisi`() {
        val k = UjiDb.konfigurasi()
        ujiAplikasi(k) { klien ->
            klien.get("/sehat")
            seed(k)
            assertEquals("1", nilaiDb("select count(*) from verifikasi where status = 'diperiksa'"))
            assertEquals("6", nilaiDb("select count(*) from notifikasi n join pengguna p on p.id = n.pengguna_id where p.email = 'penyewa@rentech.test'"))
            assertEquals("2026-11-16", nilaiDb("select (dibuat at time zone 'Asia/Jakarta')::date from notifikasi where jenis = 'terverifikasi'"))
            assertEquals("2", nilaiDb("select count(*) from notifikasi where not dibaca"))
            assertEquals("{\"Foto KTP buram, nomor NIK tidak terbaca\",\"Wajah pada swafoto tertutup bayangan\"}", nilaiDb("select alasan from verifikasi where status = 'ditolak'"))
        }
    }
}
