package com.rentech.server.akun

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.rentech.server.Gagal
import com.rentech.server.Konfigurasi
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import java.time.Duration
import java.util.Date

private const val PENERBIT = "rentech"
private val MASA_BERLAKU = Duration.ofDays(30)

fun buatToken(k: Konfigurasi, penggunaId: Int): String = JWT.create()
    .withIssuer(PENERBIT)
    .withClaim("id", penggunaId)
    .withExpiresAt(Date(System.currentTimeMillis() + MASA_BERLAKU.toMillis()))
    .sign(Algorithm.HMAC256(k.jwtSecret))

/** Pasang autentikasi "app" (JWT di header Authorization) untuk aplikasi Android. */
fun Application.pasangAuth(k: Konfigurasi) {
    authentication {
        jwt("app") {
            verifier(JWT.require(Algorithm.HMAC256(k.jwtSecret)).withIssuer(PENERBIT).build())
            validate { kred -> kred.payload.getClaim("id").asInt()?.let { JWTPrincipal(kred.payload) } }
        }
    }
}

/** Id pengguna dari token. Hanya dipanggil di dalam authenticate("app"). */
fun ApplicationCall.penggunaId(): Int =
    principal<JWTPrincipal>()?.payload?.getClaim("id")?.asInt()
        ?: throw Gagal(HttpStatusCode.Unauthorized, "Silakan masuk dulu")
