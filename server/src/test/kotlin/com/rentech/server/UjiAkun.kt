package com.rentech.server

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

/** Kirim KTP + swafoto lewat API. */
suspend fun HttpClient.kirimVerifikasi(token: String?, ktp: ByteArray, swafoto: ByteArray, nik: String = "3171000000000001") =
    submitFormWithBinaryData(
        url = "/verifikasi",
        formData = formData {
            append("nik", nik)
            append("ktp", ktp, Headers.build { append(HttpHeaders.ContentDisposition, "filename=ktp.jpg") })
            append("swafoto", swafoto, Headers.build { append(HttpHeaders.ContentDisposition, "filename=swafoto.jpg") })
        },
    ) { token?.let { bearerAuth(it) } }

/** Satu nilai dari DB untuk pemeriksaan tes. */
fun nilaiDb(sql: String): String? = UjiDb.ds.connection.use { c ->
    c.createStatement().executeQuery(sql).let { if (it.next()) it.getString(1) else null }
}
