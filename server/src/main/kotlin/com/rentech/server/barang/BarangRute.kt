package com.rentech.server.barang

import com.rentech.server.Gagal
import com.rentech.server.akun.penggunaId
import com.rentech.server.db.Barang
import com.rentech.server.db.FotoBarang
import com.rentech.server.db.Kategori
import com.rentech.server.db.Pengguna
import com.rentech.server.db.Pesanan
import com.rentech.server.foto.Penyimpanan
import com.rentech.server.foto.terimaMultipart
import com.rentech.server.pesanan.STATUS_MEMBLOKIR
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.LocalDate

@Serializable
data class PenggunaRingkasDto(val id: Int, val nama: String, val foto: String?, val terverifikasi: Boolean)

@Serializable
data class BarangDto(
    val id: Int,
    val nama: String,
    val kategori: String,
    val foto: List<String>,
    val hargaPerHari: Long,
    val deposit: Long,
    val jarak: String,
    val kelengkapan: String,
    val lokasi: String,
    val pemilik: PenggunaRingkasDto,
)

@Serializable
data class ProfilPemilikDto(val pemilik: PenggunaRingkasDto, val barang: List<BarangDto>)

fun urlFoto(path: String?) = path?.let { "/foto/$it" }

fun ringkas(r: ResultRow) = PenggunaRingkasDto(
    id = r[Pengguna.id].value,
    nama = r[Pengguna.nama],
    foto = urlFoto(r[Pengguna.foto]),
    terverifikasi = r[Pengguna.statusVerifikasi] == "terverifikasi",
)

/** Barang beserta kategori, pemilik, dan fotonya, sesuai filter. Harus dipanggil di dalam transaksi. */
fun daftarBarang(filter: Op<Boolean>): List<BarangDto> {
    val baris = Barang
        .join(Kategori, JoinType.INNER, Barang.kategori, Kategori.id)
        .join(Pengguna, JoinType.INNER, Barang.pemilik, Pengguna.id)
        .selectAll().where { filter and (Barang.tersedia eq true) }
        .orderBy(Barang.id, SortOrder.ASC)
        .toList()
    val ids = baris.map { it[Barang.id].value }
    val foto = if (ids.isEmpty()) emptyMap() else FotoBarang.selectAll()
        .where { FotoBarang.barang inList ids }
        .orderBy(FotoBarang.urutan, SortOrder.ASC)
        .groupBy({ it[FotoBarang.barang].value }, { urlFoto(it[FotoBarang.path])!! })
    return baris.map {
        val id = it[Barang.id].value
        BarangDto(
            id = id,
            nama = it[Barang.nama],
            kategori = it[Kategori.nama],
            foto = foto[id].orEmpty(),
            hargaPerHari = it[Barang.hargaPerHari],
            deposit = it[Barang.deposit],
            jarak = it[Barang.jarakDemo],
            kelengkapan = it[Barang.kelengkapan],
            lokasi = it[Barang.lokasi],
            pemilik = ringkas(it),
        )
    }
}

fun barangAtau404(id: Int): BarangDto = transaction { daftarBarang(Barang.id eq id).firstOrNull() }
    ?: throw Gagal(HttpStatusCode.NotFound, "Barang tidak ditemukan")

/** Pastikan pengguna sudah lolos verifikasi identitas sebelum bertransaksi. */
fun pastikanTerverifikasi(penggunaId: Int) {
    val status = transaction {
        Pengguna.select(Pengguna.statusVerifikasi).where { Pengguna.id eq penggunaId }.firstOrNull()?.get(Pengguna.statusVerifikasi)
    }
    if (status != "terverifikasi") throw Gagal(HttpStatusCode.Forbidden, "Verifikasi identitas dulu sebelum bertransaksi")
}

fun Route.rutBarang(penyimpanan: Penyimpanan) {
    get("/kategori") {
        call.respond(transaction { Kategori.selectAll().orderBy(Kategori.urutan to SortOrder.ASC, Kategori.id to SortOrder.ASC).map { it[Kategori.nama] } })
    }

    get("/barang") {
        val q = call.request.queryParameters["q"]?.trim().orEmpty()
        val kategori = call.request.queryParameters["kategori"]?.trim().orEmpty()
        val hargaMaks = call.request.queryParameters["hargaMaks"]?.toLongOrNull()
        val hasil = transaction {
            var f: Op<Boolean> = Op.TRUE
            if (q.isNotEmpty()) f = f and (Barang.nama.lowerCase() like "%${q.lowercase().replace("%", "\\%").replace("_", "\\_")}%")
            if (kategori.isNotEmpty()) f = f and (Kategori.nama eq kategori)
            if (hargaMaks != null) f = f and (Barang.hargaPerHari lessEq hargaMaks)
            daftarBarang(f)
        }
        call.respond(hasil)
    }

    get("/barang/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Barang tidak ditemukan")
        call.respond(barangAtau404(id))
    }

    get("/barang/{id}/tanggal-terpakai") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Barang tidak ditemukan")
        val hariIni = LocalDate.now()
        val tanggal = transaction {
            Pesanan.select(Pesanan.mulai, Pesanan.selesai)
                .where { (Pesanan.barang eq id) and (Pesanan.status inList STATUS_MEMBLOKIR) and (Pesanan.selesai greaterEq hariIni) }
                .flatMap { r -> generateSequence(r[Pesanan.mulai]) { it.plusDays(1) }.takeWhile { !it.isAfter(r[Pesanan.selesai]) }.toList() }
                .distinct().sorted().map { it.toString() }
        }
        call.respond(tanggal)
    }

    get("/pemilik/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Pemilik tidak ditemukan")
        val profil = transaction {
            val p = Pengguna.selectAll().where { Pengguna.id eq id }.firstOrNull() ?: throw Gagal(HttpStatusCode.NotFound, "Pemilik tidak ditemukan")
            ProfilPemilikDto(ringkas(p), daftarBarang(Barang.pemilik eq id))
        }
        call.respond(profil)
    }

    authenticate("app") {
        post("/barang") {
            val id = call.penggunaId()
            pastikanTerverifikasi(id)
            val m = call.terimaMultipart()
            val nama = m.teksWajib("nama").trim()
            val kategori = m.teksWajib("kategori").trim()
            val harga = m.teksWajib("hargaPerHari").toLongOrNull()?.takeIf { it > 0 } ?: throw Gagal(HttpStatusCode.BadRequest, "Harga per hari tidak valid")
            val deposit = m.teks["deposit"]?.toLongOrNull()?.takeIf { it >= 0 } ?: 0
            val fotoMasuk = m.file.filterKeys { it.startsWith("foto") }.toSortedMap().values.toList()
            if (fotoMasuk.isEmpty()) throw Gagal(HttpStatusCode.BadRequest, "Tambahkan minimal satu foto barang")
            val path = fotoMasuk.map { penyimpanan.simpan(it, privat = false) }
            val barangId = transaction {
                val kategoriId = Kategori.select(Kategori.id).where { Kategori.nama eq kategori }.firstOrNull()?.get(Kategori.id)
                    ?: Kategori.insertAndGetId { it[Kategori.nama] = kategori; it[urutan] = 99 }
                val baru = Barang.insertAndGetId {
                    it[pemilik] = id
                    it[Barang.kategori] = kategoriId
                    it[Barang.nama] = nama
                    it[hargaPerHari] = harga
                    it[Barang.deposit] = deposit
                    it[kelengkapan] = m.teks["kelengkapan"].orEmpty().trim()
                }.value
                path.forEachIndexed { i, p -> FotoBarang.insert { it[barang] = baru; it[FotoBarang.path] = p; it[urutan] = i } }
                baru
            }
            call.respond(HttpStatusCode.Created, barangAtau404(barangId))
        }
    }
}
