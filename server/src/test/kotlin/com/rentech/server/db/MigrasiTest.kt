package com.rentech.server.db

import com.rentech.server.UjiDb
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MigrasiTest {
    @Test
    fun `migrasi membuat sebelas tabel`() {
        val ada = UjiDb.ds.connection.use { c ->
            val rs = c.createStatement().executeQuery(
                "select table_name from information_schema.tables where table_schema='public' and table_name <> 'flyway_schema_history'"
            )
            buildSet { while (rs.next()) add(rs.getString(1)) }
        }
        assertEquals(
            setOf(
                "pengguna", "verifikasi", "kategori", "barang", "foto_barang", "pesanan",
                "ulasan", "pesan", "tiket_bantuan", "pesan_bantuan", "notifikasi",
            ),
            ada,
        )
    }

    @Test
    fun `status verifikasi di luar pilihan ditolak database`() {
        UjiDb.bersihkan()
        assertFailsWith<SQLException> {
            UjiDb.ds.connection.use { c ->
                c.createStatement().execute(
                    "insert into pengguna (nama, email, no_hp, sandi_hash, status_verifikasi) values ('a','a@a','1','x','asal')"
                )
            }
        }
    }
}
