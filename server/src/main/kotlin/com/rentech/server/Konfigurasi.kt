package com.rentech.server

import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

/** Semua pengaturan server. Rahasia hanya dibaca dari environment, tidak pernah dari repo. */
data class Konfigurasi(
    val jdbcUrl: String,
    val dbUser: String,
    val dbSandi: String,
    val jwtSecret: String,
    val adminEmail: String?,
    val adminSandi: String?,
    val otpDev: Boolean,
    val fotoDir: Path,
) {
    companion object {
        fun dariEnv(env: Map<String, String> = System.getenv()): Konfigurasi {
            fun wajib(nama: String) = env[nama]?.takeIf { it.isNotBlank() }
                ?: error("Variabel environment $nama belum diisi")
            val (jdbc, user, sandi) = ubahDatabaseUrl(wajib("DATABASE_URL"))
            return Konfigurasi(
                jdbcUrl = jdbc,
                dbUser = user,
                dbSandi = sandi,
                jwtSecret = wajib("JWT_SECRET"),
                adminEmail = env["ADMIN_EMAIL"]?.takeIf { it.isNotBlank() },
                adminSandi = env["ADMIN_SANDI"]?.takeIf { it.isNotBlank() },
                otpDev = env["OTP_DEV"] == "true",
                fotoDir = Paths.get(env["FOTO_DIR"] ?: "data/foto"),
            )
        }
    }
}

/** "postgresql://user:sandi@host:port/db" (format Railway) → (JDBC URL, user, sandi). */
fun ubahDatabaseUrl(url: String): Triple<String, String, String> {
    val uri = URI(url.replaceFirst(Regex("^postgres(ql)?://"), "postgresql://"))
    val (user, sandi) = (uri.rawUserInfo ?: ":").split(":", limit = 2).map { java.net.URLDecoder.decode(it, Charsets.UTF_8) }
    val port = if (uri.port == -1) 5432 else uri.port
    val query = uri.rawQuery?.let { "?$it" } ?: ""
    return Triple("jdbc:postgresql://${uri.host}:$port${uri.rawPath}$query", user, sandi)
}
