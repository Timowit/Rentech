package com.rentech.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class AplikasiTest {
    @Test
    fun `sehat mengembalikan ok`() = testApplication {
        application { modulDasar() }
        val r = client.get("/sehat")
        assertEquals(HttpStatusCode.OK, r.status)
        assertEquals("""{"status":"ok"}""", r.bodyAsText())
    }
}
