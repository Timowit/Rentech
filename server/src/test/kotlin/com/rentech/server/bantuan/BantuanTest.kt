package com.rentech.server.bantuan

import com.rentech.server.akunBaru
import com.rentech.server.aturPengguna
import com.rentech.server.idPengguna
import com.rentech.server.postJson
import com.rentech.server.sosial.PesanDto
import com.rentech.server.ujiAplikasi
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class BantuanTest {
    @Test
    fun `tiket verifikasi langsung berisi pesan pengguna dan balasan baku`() = ujiAplikasi { klien ->
        val token = klien.akunBaru("a@uji.test")
        val tiket = klien.postJson("/bantuan", token, mapOf("topik" to "Verifikasi", "teks" to "KTP saya ditolak")).body<TiketDto>()
        assertEquals(
            listOf(
                "KTP saya ditolak" to true,
                "Terima kasih, kami cek dulu ya. Kamu bisa kirim ulang foto KTP lewat tombol di bawah. Pastikan semua tulisan terbaca dan tidak ada pantulan cahaya." to false,
            ),
            tiket.pesan.map { it.teks to it.keluar },
        )
    }

    @Test
    fun `balasan admin muncul sebagai pesan masuk`() = ujiAplikasi { klien ->
        val token = klien.akunBaru("a@uji.test")
        klien.akunBaru("admin@uji.test"); aturPengguna("admin@uji.test", "admin", true)
        val tiket = klien.postJson("/bantuan", token, mapOf("topik" to "Lainnya")).body<TiketDto>()
        val layanan = BantuanLayanan()
        assertEquals(listOf(tiket.id), layanan.daftarTerbuka().map { it.id })
        layanan.balas(tiket.id, idPengguna("admin@uji.test"), "Halo, ada yang bisa kami bantu?")
        val pesan = klien.get("/bantuan/${tiket.id}") { bearerAuth(token) }.body<List<PesanDto>>()
        assertEquals("Halo, ada yang bisa kami bantu?" to false, pesan.last().let { it.teks to it.keluar })
    }

    @Test
    fun `tiket orang lain tidak terlihat`() = ujiAplikasi { klien ->
        val a = klien.akunBaru("a@uji.test")
        val b = klien.akunBaru("b@uji.test")
        val tiket = klien.postJson("/bantuan", a, mapOf("topik" to "Lainnya")).body<TiketDto>()
        assertEquals(HttpStatusCode.NotFound, klien.get("/bantuan/${tiket.id}") { bearerAuth(b) }.status)
    }

    @Test
    fun `topik tidak dikenal ditolak`() = ujiAplikasi { klien ->
        val a = klien.akunBaru("a@uji.test")
        assertEquals(HttpStatusCode.BadRequest, klien.postJson("/bantuan", a, mapOf("topik" to "Asal")).status)
    }
}
