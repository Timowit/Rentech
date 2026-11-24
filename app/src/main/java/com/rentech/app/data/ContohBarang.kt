package com.rentech.app.data

import androidx.annotation.DrawableRes
import com.rentech.app.R
import java.util.Locale

/** Barang yang disewakan. Untuk sementara isinya data contoh dari desain. */
data class Barang(
    val id: Int,
    val nama: String,
    val kategori: String,
    @DrawableRes val foto: Int,
    val hargaPerHari: Long,
    val deposit: Long,
    val jarak: String,
    val kelengkapan: String,
    val lokasi: String = "Jakarta Selatan",
)

val kategoriBarang = listOf("Kamera", "Laptop", "Proyektor", "Drone", "Audio", "Konsol game", "Gimbal", "Tablet")

val contohBarang = listOf(
    Barang(1, "Kamera mirrorless full-frame", "Kamera", R.drawable.foto_kamera, 350_000, 2_000_000, "1,2 km", "Lensa kit, baterai dua, kartu memori 64 GB"),
    Barang(2, "Laptop kerja 14 inci", "Laptop", R.drawable.foto_laptop, 75_000, 1_000_000, "2,4 km", "Pengisi daya dan tas laptop"),
    Barang(3, "Proyektor portabel", "Proyektor", R.drawable.foto_proyektor, 120_000, 1_000_000, "3,1 km", "Kabel HDMI, remote, dan tripod mini"),
    Barang(4, "Drone lipat 4K", "Drone", R.drawable.foto_drone, 200_000, 3_000_000, "2,8 km", "Remote, tiga baterai, dan tas"),
    Barang(5, "Konsol game + 2 stik", "Konsol game", R.drawable.foto_konsol, 90_000, 1_500_000, "3,5 km", "Dua stik, kabel HDMI, dan pengisi daya"),
    Barang(6, "Gimbal stabilizer", "Gimbal", R.drawable.foto_gimbal, 45_000, 500_000, "1,8 km", "Tripod mini dan kabel pengisi daya"),
    Barang(7, "Headphone peredam bising", "Audio", R.drawable.foto_headphone, 40_000, 500_000, "3,3 km", "Kabel audio dan tas"),
)

fun cariBarang(id: Int): Barang = contohBarang.first { it.id == id }

/** 350000 → "Rp350.000". */
fun rupiah(nilai: Long): String = "Rp" + String.format(Locale.US, "%,d", nilai).replace(',', '.')
