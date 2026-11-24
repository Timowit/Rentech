package com.rentech.server.barang

import com.rentech.server.akunBaru
import com.rentech.server.akunTerverifikasi
import com.rentech.server.buatBarang
import com.rentech.server.foto.JPEG_KECIL
import com.rentech.server.idPengguna
import com.rentech.server.ujiAplikasi
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
import kotlin.test.assertTrue

class BarangTest {
    @Test
    fun `cari kamera tanpa peduli huruf besar`() = ujiAplikasi { klien ->
        klien.akunBaru("p@uji.test")
        buatBarang("p@uji.test", "Kamera mirrorless full-frame", 350_000, 2_000_000)
        buatBarang("p@uji.test", "Proyektor portabel", 120_000, 1_000_000, "Proyektor")
        val hasil = klien.get("/barang?q=KAMERA").body<List<BarangDto>>()
        assertEquals(listOf("Kamera mirrorless full-frame"), hasil.map { it.nama })
        assertEquals(2, klien.get("/barang").body<List<BarangDto>>().size)
    }

    @Test
    fun `filter kategori dan harga maksimal`() = ujiAplikasi { klien ->
        klien.akunBaru("p@uji.test")
        buatBarang("p@uji.test", "Kamera", 350_000, 0)
        buatBarang("p@uji.test", "Proyektor", 120_000, 0, "Proyektor")
        assertEquals(listOf("Proyektor"), klien.get("/barang?kategori=Proyektor").body<List<BarangDto>>().map { it.nama })
        assertEquals(listOf("Proyektor"), klien.get("/barang?hargaMaks=200000").body<List<BarangDto>>().map { it.nama })
    }

    @Test
    fun `detail barang tidak ada 404`() = ujiAplikasi { klien ->
        assertEquals(HttpStatusCode.NotFound, klien.get("/barang/999").status)
    }

    @Test
    fun `kategori terurut`() = ujiAplikasi { klien ->
        klien.akunBaru("p@uji.test")
        buatBarang("p@uji.test", "A", 1, 0, "Kamera")
        assertEquals(listOf("Kamera"), klien.get("/kategori").body<List<String>>())
    }

    private suspend fun io.ktor.client.HttpClient.pasangBarang(token: String) =
        submitFormWithBinaryData("/barang", formData {
            append("nama", "Drone lipat 4K"); append("kategori", "Drone")
            append("hargaPerHari", "200000"); append("deposit", "3000000"); append("kelengkapan", "Remote")
            append("foto", JPEG_KECIL, Headers.build { append(HttpHeaders.ContentDisposition, "filename=a.jpg") })
        }) { bearerAuth(token) }

    @Test
    fun `pasang barang butuh verifikasi`() = ujiAplikasi { klien ->
        val belum = klien.akunBaru("b@uji.test")
        assertEquals(HttpStatusCode.Forbidden, klien.pasangBarang(belum).status)
        val sudah = klien.akunTerverifikasi("p@uji.test")
        assertEquals(HttpStatusCode.Created, klien.pasangBarang(sudah).status)
        val barang = klien.get("/barang").body<List<BarangDto>>().single()
        assertEquals("Drone lipat 4K", barang.nama)
        assertTrue(barang.foto.single().startsWith("/foto/publik/"))
        assertEquals(HttpStatusCode.OK, klien.get(barang.foto.single()).status)
        assertEquals(idPengguna("p@uji.test"), barang.pemilik.id)
    }

    @Test
    fun `terlalu banyak file dalam satu unggahan ditolak 413`() = ujiAplikasi { klien ->
        val token = klien.akunTerverifikasi("p@uji.test")
        val r = klien.submitFormWithBinaryData("/barang", formData {
            append("nama", "Drone"); append("kategori", "Drone"); append("hargaPerHari", "1")
            repeat(7) { i -> append("foto$i", JPEG_KECIL, Headers.build { append(HttpHeaders.ContentDisposition, "filename=$i.jpg") }) }
        }) { bearerAuth(token) }
        assertEquals(HttpStatusCode.PayloadTooLarge, r.status)
        assertEquals(0, klien.get("/barang").body<List<BarangDto>>().size)
    }

    @Test
    fun `profil pemilik berisi barangnya`() = ujiAplikasi { klien ->
        klien.akunBaru("p@uji.test")
        buatBarang("p@uji.test", "Kamera", 350_000, 0)
        val profil = klien.get("/pemilik/${idPengguna("p@uji.test")}").body<ProfilPemilikDto>()
        assertEquals(listOf("Kamera"), profil.barang.map { it.nama })
    }
}
