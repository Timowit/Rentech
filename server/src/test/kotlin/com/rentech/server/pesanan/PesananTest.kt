package com.rentech.server.pesanan

import com.rentech.server.Gagal
import com.rentech.server.UjiDb
import com.rentech.server.akunBaru
import com.rentech.server.akunTerverifikasi
import com.rentech.server.aturPengguna
import com.rentech.server.buatBarang
import com.rentech.server.foto.JPEG_KECIL
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.idPengguna
import com.rentech.server.nilaiDb
import com.rentech.server.notifikasi.NotifikasiLayanan
import com.rentech.server.pesananBaru
import com.rentech.server.postJson
import com.rentech.server.ujiAplikasi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PesananTest {
    private val k = UjiDb.konfigurasi()
    private val layanan = PesananLayanan(Penyimpanan(k.fotoDir), NotifikasiLayanan())

    private suspend fun HttpClient.siapkan(): Triple<String, String, Int> {
        val pemilik = akunTerverifikasi("p@uji.test")
        val penyewa = akunTerverifikasi("s@uji.test")
        val barang = buatBarang("p@uji.test", "Kamera mirrorless full-frame", 350_000, 2_000_000)
        return Triple(pemilik, penyewa, barang)
    }

    @Test
    fun `pengguna belum terverifikasi tidak bisa memesan`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("p@uji.test")
        val barang = buatBarang("p@uji.test", "Kamera", 350_000, 0)
        val token = klien.akunBaru("s@uji.test")
        assertEquals(HttpStatusCode.Forbidden, klien.postJson("/pesanan", token, pesananBaru(barang, "2026-12-12", "2026-12-15")).status)
    }

    @Test
    fun `tanggal tumpang tindih ditolak 409`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        assertEquals(HttpStatusCode.Created, klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).status)
        assertEquals(HttpStatusCode.Conflict, klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-14", "2026-12-16")).status)
    }

    @Test
    fun `pesanan yang ditolak pemilik tidak memblokir tanggal`() = ujiAplikasi(k) { klien ->
        val (pemilik, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).body<PesananDto>()
        assertEquals(HttpStatusCode.OK, klien.postJson("/pesanan/${p.id}/tolak", pemilik, emptyMap()).status)
        assertEquals(HttpStatusCode.Created, klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-14", "2026-12-16")).status)
    }

    @Test
    fun `tanggal selesai harus setelah mulai`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        assertEquals(HttpStatusCode.BadRequest, klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-15", "2026-12-15")).status)
    }

    @Test
    fun `total tiga hari kamera tunai ambil sendiri`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).body<PesananDto>()
        assertEquals(3_075_000, p.total)
        assertEquals("tunai", p.statusDana)
    }

    @Test
    fun `transfer menambah kode unik`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15", metode = "transfer", diantar = true)).body<PesananDto>()
        val kode = assertNotNull(p.kodeUnik)
        assertTrue(kode in 100..999)
        assertEquals(3_100_000L + kode, p.total)
    }

    @Test
    fun `tanggal terpakai berisi hari pesanan aktif`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-20", "2026-12-22"))
        assertEquals(listOf("2026-12-20", "2026-12-21", "2026-12-22"), klien.get("/barang/$barang/tanggal-terpakai").body<List<String>>())
    }

    @Test
    fun `bukan pemilik tidak bisa menerima`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).body<PesananDto>()
        assertEquals(HttpStatusCode.Forbidden, klien.postJson("/pesanan/${p.id}/terima", penyewa, emptyMap()).status)
    }

    @Test
    fun `pemilik menerima lalu penyewa mendapat notifikasi`() = ujiAplikasi(k) { klien ->
        val (pemilik, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).body<PesananDto>()
        klien.postJson("/pesanan/${p.id}/terima", pemilik, emptyMap())
        assertEquals("Pemilik menerima pesananmu", nilaiDb("select judul from notifikasi where pengguna_id = ${idPengguna("s@uji.test")}"))
        assertEquals(listOf(p.id), klien.get("/pesanan/masuk") { bearerAuth(pemilik) }.body<List<PesananDto>>().map { it.id })
        assertEquals("diterima_pemilik", klien.get("/pesanan/saya") { bearerAuth(penyewa) }.body<List<PesananDto>>().single().status)
    }

    @Test
    fun `bukti transfer diperiksa admin lalu dana ditahan dan diteruskan`() = ujiAplikasi(k) { klien ->
        val (pemilik, penyewa, barang) = klien.siapkan()
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15", metode = "transfer")).body<PesananDto>()
        val r = klien.submitFormWithBinaryData("/pesanan/${p.id}/bukti", formData {
            append("bukti", JPEG_KECIL, Headers.build { append(HttpHeaders.ContentDisposition, "filename=b.jpg") })
        }) { bearerAuth(penyewa) }
        assertEquals(HttpStatusCode.OK, r.status)
        assertEquals("menunggu_cek", nilaiDb("select status_dana from pesanan"))
        assertTrue(nilaiDb("select bukti_transfer from pesanan")!!.startsWith("privat/"))

        layanan.periksaBukti(p.id, idPengguna("admin@uji.test"), sesuai = true, alasan = null)
        assertEquals("ditahan", nilaiDb("select status_dana from pesanan"))
        assertEquals("1", nilaiDb("select count(*) from notifikasi where judul = 'Bukti transfer sudah diperiksa'"))

        klien.postJson("/pesanan/${p.id}/terima", pemilik, emptyMap())
        assertEquals(HttpStatusCode.OK, klien.postJson("/pesanan/${p.id}/diterima", penyewa, emptyMap()).status)
        assertEquals("diteruskan", nilaiDb("select status_dana from pesanan"))
        assertEquals("berjalan", nilaiDb("select status from pesanan"))
    }

    @Test
    fun `periksa bukti oleh bukan admin ditolak`() = ujiAplikasi(k) { klien ->
        val (_, penyewa, barang) = klien.siapkan()
        val p = klien.postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15", metode = "transfer")).body<PesananDto>()
        val e = assertFailsWith<Gagal> { layanan.periksaBukti(p.id, idPengguna("s@uji.test"), sesuai = true, alasan = null) }
        assertEquals(HttpStatusCode.Forbidden, e.status)
    }
}
