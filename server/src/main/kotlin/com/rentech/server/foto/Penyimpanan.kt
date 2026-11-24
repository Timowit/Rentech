package com.rentech.server.foto

import com.rentech.server.Gagal
import io.ktor.http.HttpStatusCode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

const val BATAS_FOTO = 5 * 1024 * 1024

/**
 * Menyimpan foto di disk (volume Railway di produksi).
 * Folder publik/ dilayani lewat /foto, folder privat/ hanya lewat rute admin.
 */
class Penyimpanan(private val dir: Path) {

    /** Foto unggahan pengguna: hanya JPEG atau PNG, maksimal 5 MB. Mengembalikan path relatif. */
    fun simpan(bytes: ByteArray, privat: Boolean): String {
        if (bytes.size > BATAS_FOTO) throw Gagal(HttpStatusCode.PayloadTooLarge, "Ukuran foto maksimal 5 MB")
        val ekstensi = jenisGambar(bytes) ?: throw Gagal(HttpStatusCode.UnsupportedMediaType, "Foto harus berformat JPG atau PNG")
        return tulis(bytes, ekstensi, privat)
    }

    /** Untuk seed: file dari repo sendiri (misalnya WebP), tanpa pemeriksaan unggahan. */
    fun simpanTerpercaya(bytes: ByteArray, ekstensi: String, privat: Boolean): String = tulis(bytes, ekstensi, privat)

    /** File di dalam folder foto, atau null kalau path keluar dari folder atau tidak ada. */
    fun baca(path: String): Path? {
        val akar = dir.toAbsolutePath().normalize()
        val file = akar.resolve(path).normalize()
        return file.takeIf { it.startsWith(akar) && Files.isRegularFile(it) }
    }

    private fun tulis(bytes: ByteArray, ekstensi: String, privat: Boolean): String {
        val relatif = "${if (privat) "privat" else "publik"}/${UUID.randomUUID()}.$ekstensi"
        val tujuan = dir.resolve(relatif)
        Files.createDirectories(tujuan.parent)
        val sementara = Files.createTempFile(tujuan.parent, ".unggah", ".tmp")
        try {
            Files.write(sementara, bytes)
            Files.move(sementara, tujuan, StandardCopyOption.ATOMIC_MOVE)
        } finally {
            Files.deleteIfExists(sementara)
        }
        return relatif
    }
}

/** Kenali format dari magic bytes, bukan dari nama file. */
fun jenisGambar(b: ByteArray): String? = when {
    b.size >= 3 && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte() && b[2] == 0xFF.toByte() -> "jpg"
    b.size >= 4 && b[0] == 0x89.toByte() && b[1] == 0x50.toByte() && b[2] == 0x4E.toByte() && b[3] == 0x47.toByte() -> "png"
    else -> null
}

fun tipeKonten(path: String): String = when (path.substringAfterLast('.')) {
    "jpg" -> "image/jpeg"
    "png" -> "image/png"
    "webp" -> "image/webp"
    else -> "application/octet-stream"
}
