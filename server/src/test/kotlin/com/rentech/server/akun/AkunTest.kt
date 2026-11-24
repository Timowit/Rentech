package com.rentech.server.akun

import com.rentech.server.UjiDb
import com.rentech.server.akunBaru
import com.rentech.server.postJson
import com.rentech.server.ujiAplikasi
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AkunTest {
    @Test
    fun `daftar lalu otp lalu saya berstatus belum`() = ujiAplikasi { klien ->
        val token = klien.akunBaru("a@uji.test")
        val saya = klien.get("/akun/saya") { bearerAuth(token) }.body<PenggunaDto>()
        assertEquals("belum", saya.statusVerifikasi)
        assertEquals("a@uji.test", saya.email)
    }

    @Test
    fun `otp salah ditolak`() = ujiAplikasi { klien ->
        klien.postJson("/akun/daftar", null, mapOf("nama" to "A", "email" to "a@uji.test", "noHp" to "1", "sandi" to "rahasia123"))
        val r = klien.postJson("/akun/otp", null, mapOf("email" to "a@uji.test", "kode" to "000000"))
        assertEquals(HttpStatusCode.Unauthorized, r.status)
    }

    @Test
    fun `saya tanpa token ditolak`() = ujiAplikasi { klien ->
        assertEquals(HttpStatusCode.Unauthorized, klien.get("/akun/saya").status)
    }

    @Test
    fun `email ganda ditolak`() = ujiAplikasi { klien ->
        val badan = mapOf("nama" to "A", "email" to "a@uji.test", "noHp" to "1", "sandi" to "rahasia123")
        assertEquals(HttpStatusCode.Created, klien.postJson("/akun/daftar", null, badan).status)
        assertEquals(HttpStatusCode.Conflict, klien.postJson("/akun/daftar", null, badan).status)
    }

    @Test
    fun `masuk dengan sandi benar dan salah`() = ujiAplikasi { klien ->
        klien.akunBaru("a@uji.test")
        assertEquals(HttpStatusCode.OK, klien.postJson("/akun/masuk", null, mapOf("email" to "a@uji.test", "sandi" to "rahasia123")).status)
        assertEquals(HttpStatusCode.Unauthorized, klien.postJson("/akun/masuk", null, mapOf("email" to "a@uji.test", "sandi" to "salah")).status)
    }

    @Test
    fun `patch tidak bisa menjadikan admin`() = ujiAplikasi { klien ->
        val token = klien.akunBaru("a@uji.test")
        klien.patch("/akun/saya") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(mapOf("peranAktif" to "pemilik", "admin" to "true"))
        }
        val admin = UjiDb.ds.connection.use { c ->
            c.createStatement().executeQuery("select admin from pengguna where email='a@uji.test'").let { it.next(); it.getBoolean(1) }
        }
        assertFalse(admin)
        assertEquals("pemilik", klien.get("/akun/saya") { bearerAuth(token) }.body<PenggunaDto>().peranAktif)
    }

    @Test
    fun `peran di luar pilihan ditolak`() = ujiAplikasi { klien ->
        val token = klien.akunBaru("a@uji.test")
        val r = klien.patch("/akun/saya") {
            bearerAuth(token); contentType(ContentType.Application.Json); setBody(mapOf("peranAktif" to "raja"))
        }
        assertEquals(HttpStatusCode.BadRequest, r.status)
    }
}
