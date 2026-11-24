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


suspend fun HttpClient.postJson(url: String, token: String?, body: Map<String, Any?>) = post(url) {
    token?.let { bearerAuth(it) }
    contentType(ContentType.Application.Json)
    setBody(keJson(body))
}

fun keJson(m: Map<String, Any?>): kotlinx.serialization.json.JsonObject = kotlinx.serialization.json.buildJsonObject {
    m.forEach { (k, v) ->
        when (v) {
            null -> put(k, kotlinx.serialization.json.JsonNull)
            is Number -> put(k, kotlinx.serialization.json.JsonPrimitive(v))
            is Boolean -> put(k, kotlinx.serialization.json.JsonPrimitive(v))
            is List<*> -> put(k, kotlinx.serialization.json.JsonArray(v.map { kotlinx.serialization.json.JsonPrimitive(it.toString()) }))
            else -> put(k, kotlinx.serialization.json.JsonPrimitive(v.toString()))
        }
    }
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

/** Kategori + barang langsung di DB. Mengembalikan id barang. */
fun buatBarang(pemilikEmail: String, nama: String, harga: Long, deposit: Long, kategori: String = "Kamera"): Int =
    UjiDb.ds.connection.use { c ->
        c.prepareStatement("insert into kategori (nama, urutan) values (?, 0) on conflict (nama) do nothing").apply { setString(1, kategori); executeUpdate() }
        c.prepareStatement(
            "insert into barang (pemilik_id, kategori_id, nama, harga_per_hari, deposit, jarak_demo) " +
                "values (?, (select id from kategori where nama = ?), ?, ?, ?, '1,2 km') returning id"
        ).apply {
            setInt(1, idPengguna(pemilikEmail)); setString(2, kategori); setString(3, nama); setLong(4, harga); setLong(5, deposit)
        }.executeQuery().let { it.next(); it.getInt(1) }
    }

/** Akun yang sudah terverifikasi, siap bertransaksi. */
suspend fun HttpClient.akunTerverifikasi(email: String): String =
    akunBaru(email).also { aturPengguna(email, "status_verifikasi", "terverifikasi") }

fun pesananBaru(barangId: Int, mulai: String, selesai: String, metode: String = "tunai", diantar: Boolean = false) =
    mapOf("barangId" to barangId, "mulai" to mulai, "selesai" to selesai, "diantar" to diantar, "slot" to 0, "metode" to metode)
