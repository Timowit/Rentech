package com.rentech.server.foto

import com.rentech.server.Gagal
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

/** Isi formulir multipart: kolom teks dan file. */
class Multipart(val teks: Map<String, String>, val file: Map<String, ByteArray>) {
    fun fileWajib(nama: String) = file[nama] ?: throw Gagal(HttpStatusCode.BadRequest, "Foto $nama belum diunggah")
    fun teksWajib(nama: String) = teks[nama]?.takeIf { it.isNotBlank() } ?: throw Gagal(HttpStatusCode.BadRequest, "Kolom $nama belum diisi")
}

/** Batas file per unggahan: cukup untuk 5 foto barang, atau KTP + swafoto. */
const val BATAS_JUMLAH_FILE = 6

/**
 * Baca multipart. Setiap file berhenti dibaca setelah 5 MB + 1 byte, dan file ke-7 menghentikan permintaan,
 * jadi satu unggahan tidak bisa memenuhi memori server.
 */
suspend fun ApplicationCall.terimaMultipart(): Multipart {
    val teks = mutableMapOf<String, String>()
    val file = mutableMapOf<String, ByteArray>()
    receiveMultipart(formFieldLimit = BATAS_FOTO + 1L).forEachPart { part ->
        val nama = part.name
        when (part) {
            is PartData.FormItem -> if (nama != null) teks[nama] = part.value
            is PartData.FileItem -> if (nama != null) {
                if (file.size >= BATAS_JUMLAH_FILE) {
                    part.dispose()
                    throw Gagal(HttpStatusCode.PayloadTooLarge, "Maksimal $BATAS_JUMLAH_FILE foto sekali unggah")
                }
                val bytes = part.provider().readRemaining(BATAS_FOTO + 1L).readByteArray()
                file[nama] = bytes
            }
            else -> {}
        }
        part.dispose()
    }
    return Multipart(teks, file)
}

/** Foto publik saja. Path privat atau yang keluar dari folder publik dianggap tidak ada. */
fun Route.rutFoto(penyimpanan: Penyimpanan) {
    get("/foto/{path...}") {
        val path = call.parameters.getAll("path").orEmpty().joinToString("/")
        val file = path.takeIf { it.startsWith("publik/") && ".." !in it }?.let { penyimpanan.baca(it) }
        if (file == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        call.response.headers.append("Cache-Control", "public, max-age=86400")
        call.respondFile(file.toFile())
    }
}
