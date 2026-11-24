package com.rentech.server

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Daftar akun baru lewat API, verifikasi OTP pengembangan, kembalikan token. */
suspend fun HttpClient.akunBaru(email: String, nama: String = "Uji"): String {
    post("/akun/daftar") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("nama" to nama, "email" to email, "noHp" to "081200000000", "sandi" to "rahasia123"))
    }
    val r = post("/akun/otp") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("email" to email, "kode" to "123456"))
    }
    return r.body<JsonObject>()["token"]!!.jsonPrimitive.content
}

/** Ubah kolom pengguna langsung di DB, untuk menyiapkan keadaan tes. */
fun aturPengguna(email: String, kolom: String, nilai: Any) {
    UjiDb.ds.connection.use { c ->
        c.prepareStatement("update pengguna set $kolom = ? where email = ?").apply {
            setObject(1, nilai); setString(2, email); executeUpdate()
        }
    }
}

fun idPengguna(email: String): Int = UjiDb.ds.connection.use { c ->
    c.prepareStatement("select id from pengguna where email = ?").apply { setString(1, email) }
        .executeQuery().let { it.next(); it.getInt(1) }
}


suspend fun HttpClient.postJson(url: String, token: String?, body: Any) = post(url) {
    token?.let { bearerAuth(it) }
    contentType(ContentType.Application.Json)
    setBody(body)
}
