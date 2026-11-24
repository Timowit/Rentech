package com.rentech.server.foto

import com.rentech.server.Gagal
import com.rentech.server.UjiDb
import com.rentech.server.ujiAplikasi
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import java.nio.file.Files
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

val JPEG_KECIL = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 1, 2, 3)
val PNG_KECIL = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 1, 2)

class PenyimpananTest {
    private fun semuaFile(p: Penyimpanan, dir: java.nio.file.Path) =
        Files.walk(dir).filter { Files.isRegularFile(it) }.toList()

    @Test
    fun `jpeg publik tersimpan dan bisa diambil lewat rute`() {
        val k = UjiDb.konfigurasi()
        val path = Penyimpanan(k.fotoDir).simpan(JPEG_KECIL, privat = false)
        assertTrue(path.startsWith("publik/") && path.endsWith(".jpg"))
        ujiAplikasi(k) { klien -> assertEquals(HttpStatusCode.OK, klien.get("/foto/$path").status) }
    }

    @Test
    fun `png privat tersimpan di folder privat`() {
        val dir = Files.createTempDirectory("foto")
        val path = Penyimpanan(dir).simpan(PNG_KECIL, privat = true)
        assertTrue(path.startsWith("privat/") && path.endsWith(".png"))
        assertTrue(dir.resolve(path).exists())
    }

    @Test
    fun `pdf ditolak 415 tanpa sisa file`() {
        val dir = Files.createTempDirectory("foto")
        val p = Penyimpanan(dir)
        val e = assertFailsWith<Gagal> { p.simpan("%PDF-1.7 isi".toByteArray(), privat = false) }
        assertEquals(HttpStatusCode.UnsupportedMediaType, e.status)
        assertEquals(0, semuaFile(p, dir).size)
    }

    @Test
    fun `lebih dari lima mb ditolak 413`() {
        val dir = Files.createTempDirectory("foto")
        val besar = ByteArray(5 * 1024 * 1024 + 1).also { JPEG_KECIL.copyInto(it) }
        val e = assertFailsWith<Gagal> { Penyimpanan(dir).simpan(besar, privat = false) }
        assertEquals(HttpStatusCode.PayloadTooLarge, e.status)
        assertEquals(0, semuaFile(Penyimpanan(dir), dir).size)
    }

    @Test
    fun `foto privat dan path keluar folder tidak dilayani`() {
        val k = UjiDb.konfigurasi()
        val privat = Penyimpanan(k.fotoDir).simpan(JPEG_KECIL, privat = true)
        ujiAplikasi(k) { klien ->
            assertEquals(HttpStatusCode.NotFound, klien.get("/foto/$privat").status)
            assertEquals(HttpStatusCode.NotFound, klien.get("/foto/publik/../$privat").status)
            assertEquals(HttpStatusCode.NotFound, klien.get("/foto/publik/%2e%2e/$privat").status)
        }
    }
}
