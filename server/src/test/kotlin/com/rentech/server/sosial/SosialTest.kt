package com.rentech.server.sosial

import com.rentech.server.akunTerverifikasi
import com.rentech.server.buatBarang
import com.rentech.server.idPengguna
import com.rentech.server.notifikasi.NotifikasiDto
import com.rentech.server.notifikasi.NotifikasiLayanan
import com.rentech.server.pesanan.PesananDto
import com.rentech.server.pesananBaru
import com.rentech.server.postJson
import com.rentech.server.ujiAplikasi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SosialTest {
    private suspend fun HttpClient.sewaBerjalan(): Triple<String, String, PesananDto> {
        val pemilik = akunTerverifikasi("p@uji.test")
        val penyewa = akunTerverifikasi("s@uji.test")
        val barang = buatBarang("p@uji.test", "Kamera", 350_000, 0)
        val p = postJson("/pesanan", penyewa, pesananBaru(barang, "2026-12-12", "2026-12-15")).body<PesananDto>()
        postJson("/pesanan/${p.id}/terima", pemilik, emptyMap())
        postJson("/pesanan/${p.id}/diterima", penyewa, emptyMap())
        return Triple(pemilik, penyewa, p)
    }

    @Test
    fun `ulasan hanya sekali per pesanan`() = ujiAplikasi { klien ->
        val (_, penyewa, p) = klien.sewaBerjalan()
        val ulas = mapOf("bintang" to 5, "isi" to "Barang sesuai foto")
        assertEquals(HttpStatusCode.Created, klien.postJson("/pesanan/${p.id}/ulasan", penyewa, ulas).status)
        assertEquals(HttpStatusCode.Conflict, klien.postJson("/pesanan/${p.id}/ulasan", penyewa, ulas).status)
    }

    @Test
    fun `bintang di luar satu sampai lima ditolak`() = ujiAplikasi { klien ->
        val (_, penyewa, p) = klien.sewaBerjalan()
        assertEquals(HttpStatusCode.BadRequest, klien.postJson("/pesanan/${p.id}/ulasan", penyewa, mapOf("bintang" to 6, "isi" to "")).status)
    }

    @Test
    fun `pesan terkirim terlihat masuk di sisi lawan`() = ujiAplikasi { klien ->
        val (pemilik, penyewa, p) = klien.sewaBerjalan()
        val barang = p.barang.id
        assertEquals(HttpStatusCode.Created, klien.postJson("/percakapan/$barang", penyewa, mapOf("teks" to "Jam 10 bisa?")).status)
        val sisiPemilik = klien.get("/percakapan/$barang?dengan=${idPengguna("s@uji.test")}") { bearerAuth(pemilik) }.body<List<PesanDto>>()
        assertEquals(listOf("Jam 10 bisa?" to false), sisiPemilik.map { it.teks to it.keluar })
        val sisiPenyewa = klien.get("/percakapan/$barang") { bearerAuth(penyewa) }.body<List<PesanDto>>()
        assertEquals(listOf(true), sisiPenyewa.map { it.keluar })
    }

    @Test
    fun `setelah hanya mengembalikan pesan yang lebih baru`() = ujiAplikasi { klien ->
        val (_, penyewa, p) = klien.sewaBerjalan()
        val barang = p.barang.id
        klien.postJson("/percakapan/$barang", penyewa, mapOf("teks" to "satu"))
        val pertama = klien.get("/percakapan/$barang") { bearerAuth(penyewa) }.body<List<PesanDto>>().last().id
        klien.postJson("/percakapan/$barang", penyewa, mapOf("teks" to "dua"))
        assertEquals(listOf("dua"), klien.get("/percakapan/$barang?setelah=$pertama") { bearerAuth(penyewa) }.body<List<PesanDto>>().map { it.teks })
    }

    @Test
    fun `pesan kosong ditolak`() = ujiAplikasi { klien ->
        val (_, penyewa, p) = klien.sewaBerjalan()
        assertEquals(HttpStatusCode.BadRequest, klien.postJson("/percakapan/${p.barang.id}", penyewa, mapOf("teks" to "   ")).status)
    }

    @Test
    fun `notifikasi hanya milik sendiri dan bisa dibaca semua`() = ujiAplikasi { klien ->
        val (pemilik, penyewa, _) = klien.sewaBerjalan()
        NotifikasiLayanan().kirim(idPengguna("p@uji.test"), "pesan", "Untuk pemilik", "isi")
        val punyaPenyewa = klien.get("/notifikasi") { bearerAuth(penyewa) }.body<List<NotifikasiDto>>()
        assertTrue(punyaPenyewa.none { it.judul == "Untuk pemilik" })
        assertTrue(punyaPenyewa.isNotEmpty())
        val milikPemilik = klien.get("/notifikasi") { bearerAuth(pemilik) }.body<List<NotifikasiDto>>().single()
        assertEquals(HttpStatusCode.NotFound, klien.postJson("/notifikasi/${milikPemilik.id}/dibaca", penyewa, emptyMap()).status)
        klien.postJson("/notifikasi/dibaca-semua", penyewa, emptyMap())
        assertTrue(klien.get("/notifikasi") { bearerAuth(penyewa) }.body<List<NotifikasiDto>>().all { it.dibaca })
        assertEquals(false, klien.get("/notifikasi") { bearerAuth(pemilik) }.body<List<NotifikasiDto>>().single().dibaca)
    }
}
