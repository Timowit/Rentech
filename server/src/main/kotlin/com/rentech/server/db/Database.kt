package com.rentech.server.db

import com.rentech.server.Konfigurasi
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun buatDataSource(k: Konfigurasi): HikariDataSource = HikariDataSource(
    HikariConfig().apply {
        jdbcUrl = k.jdbcUrl
        username = k.dbUser
        password = k.dbSandi
        maximumPoolSize = 5
    }
)

/** Jalankan semua migrasi di resources/db/migration. */
fun migrasi(ds: DataSource) {
    Flyway.configure().dataSource(ds).load().migrate()
}
