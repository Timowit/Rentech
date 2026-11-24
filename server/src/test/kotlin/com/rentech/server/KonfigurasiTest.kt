package com.rentech.server

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KonfigurasiTest {
    @Test
    fun `url railway diubah jadi jdbc beserta user dan sandi`() {
        assertEquals(
            Triple("jdbc:postgresql://h:5432/railway", "u", "p"),
            ubahDatabaseUrl("postgresql://u:p@h:5432/railway"),
        )
    }

    @Test
    fun `awalan postgres juga diterima`() {
        assertEquals(
            Triple("jdbc:postgresql://h:5432/db", "u", "p"),
            ubahDatabaseUrl("postgres://u:p@h:5432/db"),
        )
    }

    private val envDasar = mapOf(
        "DATABASE_URL" to "postgresql://u:p@h:5432/db",
        "JWT_SECRET" to "rahasia",
        "OTP_DEV" to "true",
    )

    @Test
    fun `otp dev terbaca dari env`() {
        assertTrue(Konfigurasi.dariEnv(envDasar).otpDev)
    }

    @Test
    fun `tanpa jwt secret gagal dengan pesan jelas`() {
        val e = assertFailsWith<IllegalStateException> { Konfigurasi.dariEnv(envDasar - "JWT_SECRET") }
        assertTrue(e.message!!.contains("JWT_SECRET"))
    }
}
