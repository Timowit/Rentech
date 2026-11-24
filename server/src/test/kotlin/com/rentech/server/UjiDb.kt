package com.rentech.server

import com.rentech.server.db.migrasi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import java.nio.file.Files
import javax.sql.DataSource

/** Satu Postgres sungguhan per JVM tes, sudah dimigrasi. */
object UjiDb {
    private val pg: EmbeddedPostgres by lazy { EmbeddedPostgres.start() }
    val ds: DataSource by lazy { pg.postgresDatabase.also { migrasi(it) } }

    /** Kosongkan semua tabel aplikasi, sisakan riwayat migrasi. */
    fun bersihkan() {
        ds.connection.use { c ->
            val tabel = c.createStatement().executeQuery(
                "select tablename from pg_tables where schemaname='public' and tablename <> 'flyway_schema_history'"
            ).let { rs -> buildList { while (rs.next()) add(rs.getString(1)) } }
            if (tabel.isNotEmpty()) c.createStatement().execute("truncate ${tabel.joinToString()} restart identity cascade")
        }
    }

    fun konfigurasi(adminEmail: String? = null, adminSandi: String? = null) = Konfigurasi(
        jdbcUrl = "", dbUser = "", dbSandi = "",
        jwtSecret = "rahasia-tes",
        adminEmail = adminEmail, adminSandi = adminSandi,
        otpDev = true,
        fotoDir = Files.createTempDirectory("foto-tes"),
    )
}

/** Aplikasi lengkap di atas database tes yang sudah dibersihkan. */
fun ujiAplikasi(
    k: Konfigurasi = UjiDb.konfigurasi(),
    blok: suspend ApplicationTestBuilder.(HttpClient) -> Unit,
) = testApplication {
    UjiDb.bersihkan()
    application { modul(k, UjiDb.ds) }
    val klien = createClient { install(ContentNegotiation) { json(jsonRentech) } }
    blok(klien)
}
