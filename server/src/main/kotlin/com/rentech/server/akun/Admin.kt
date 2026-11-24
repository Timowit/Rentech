package com.rentech.server.akun

import com.rentech.server.Gagal
import com.rentech.server.db.Pengguna
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/** Lempar 403 kalau pengguna bukan admin. Kolom admin hanya diisi seed, tidak pernah lewat API. */
fun pastikanAdmin(penggunaId: Int) {
    val admin = transaction {
        Pengguna.select(Pengguna.admin).where { Pengguna.id eq penggunaId }.firstOrNull()?.get(Pengguna.admin)
    }
    if (admin != true) throw Gagal(HttpStatusCode.Forbidden, "Khusus admin")
}
