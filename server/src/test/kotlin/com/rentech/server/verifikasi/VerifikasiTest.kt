package com.rentech.server.verifikasi

import com.rentech.server.Gagal
import com.rentech.server.UjiDb
import com.rentech.server.akun.PenggunaDto
import com.rentech.server.akunBaru
import com.rentech.server.aturPengguna
import com.rentech.server.foto.JPEG_KECIL
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.idPengguna
import com.rentech.server.kirimVerifikasi
import com.rentech.server.nilaiDb
import com.rentech.server.notifikasi.NotifikasiLayanan
import com.rentech.server.ujiAplikasi
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class VerifikasiTest {
    private val k = UjiDb.konfigurasi()
    private val layanan = VerifikasiLayanan(Penyimpanan(k.fotoDir), NotifikasiLayanan())

    @Test
    fun `unggah verifikasi masuk antrean dan status diperiksa`() = ujiAplikasi(k) { klien ->
        val token = klien.akunBaru("a@uji.test")
        assertEquals(HttpStatusCode.Accepted, klien.kirimVerifikasi(token, JPEG_KECIL, JPEG_KECIL).status)
        assertEquals("diperiksa", klien.get("/akun/saya") { bearerAuth(token) }.body<PenggunaDto>().statusVerifikasi)
        val antrean = layanan.antrean()
        assertEquals(1, antrean.size)
        assertTrue(antrean[0].fotoKtp.startsWith("privat/"))
        assertTrue(nilaiDb("select foto_swafoto from verifikasi")!!.startsWith("privat/"))
    }

    @Test
    fun `terima membuat terverifikasi dan mengirim notifikasi`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        val token = klien.akunBaru("a@uji.test")
        klien.kirimVerifikasi(token, JPEG_KECIL, JPEG_KECIL)
        layanan.terima(layanan.antrean()[0].id, idPengguna("admin@uji.test"))
        assertEquals("terverifikasi", klien.get("/akun/saya") { bearerAuth(token) }.body<PenggunaDto>().statusVerifikasi)
        assertEquals("Identitas kamu terverifikasi", nilaiDb("select judul from notifikasi where pengguna_id = ${idPengguna("a@uji.test")}"))
        assertEquals(0, layanan.antrean().size)
    }

    @Test
    fun `tolak menyimpan alasan yang terlihat di akun saya`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        val token = klien.akunBaru("a@uji.test")
        klien.kirimVerifikasi(token, JPEG_KECIL, JPEG_KECIL)
        val alasan = ALASAN_BAKU.take(2)
        layanan.tolak(layanan.antrean()[0].id, idPengguna("admin@uji.test"), alasan)
        val saya = klien.get("/akun/saya") { bearerAuth(token) }.body<PenggunaDto>()
        assertEquals("ditolak", saya.statusVerifikasi)
        assertEquals(alasan, saya.alasanDitolak)
        assertEquals("ditolak", nilaiDb("select jenis from notifikasi"))
    }

    @Test
    fun `tolak tanpa alasan ditolak 400`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        val token = klien.akunBaru("a@uji.test")
        klien.kirimVerifikasi(token, JPEG_KECIL, JPEG_KECIL)
        val e = assertFailsWith<Gagal> { layanan.tolak(layanan.antrean()[0].id, idPengguna("admin@uji.test"), emptyList()) }
        assertEquals(HttpStatusCode.BadRequest, e.status)
    }

    @Test
    fun `pengguna biasa bukan admin`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("a@uji.test")
        val e = assertFailsWith<Gagal> { layanan.pastikanAdmin(idPengguna("a@uji.test")) }
        assertEquals(HttpStatusCode.Forbidden, e.status)
    }

    @Test
    fun `admin lolos pemeriksaan`() = ujiAplikasi(k) { klien ->
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        layanan.pastikanAdmin(idPengguna("admin@uji.test"))
    }

    @Test
    fun `unggah tanpa token ditolak 401`() = ujiAplikasi(k) { klien ->
        assertEquals(HttpStatusCode.Unauthorized, klien.kirimVerifikasi(null, JPEG_KECIL, JPEG_KECIL).status)
    }

    @Test
    fun `unggah bukan gambar ditolak 415 dan status tetap belum`() = ujiAplikasi(k) { klien ->
        val token = klien.akunBaru("a@uji.test")
        assertEquals(HttpStatusCode.UnsupportedMediaType, klien.kirimVerifikasi(token, "%PDF".toByteArray(), JPEG_KECIL).status)
        assertEquals("belum", nilaiDb("select status_verifikasi from pengguna"))
    }
}
