package com.rentech.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.rentech.app.R

val Inter = FontFamily(
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
)

private val Roboto = FontFamily.Default

// Gaya teks Rentech/* dari Figma.
object Teks {
    val Display = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Bold,
        fontSize = 36.sp, lineHeight = 1.2.em, letterSpacing = (-0.54).sp,
    )
    val Judul = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 1.25.em, letterSpacing = (-0.33).sp,
    )
    val JudulBilah = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 1.4.em,
    )
    val Isi = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 1.45.em,
    )
    val IsiTebal = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 1.4.em,
    )
    val Tombol = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 1.4.em,
    )
    val Label = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 1.4.em,
    )
    val Keterangan = TextStyle(
        fontFamily = Roboto, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 1.4.em,
    )
}

val Typography = Typography(
    displaySmall = Teks.Display,
    titleLarge = Teks.Judul,
    titleMedium = Teks.JudulBilah,
    bodyLarge = Teks.Isi,
    bodyMedium = Teks.Isi,
    bodySmall = Teks.Keterangan,
    labelLarge = Teks.Tombol,
    labelMedium = Teks.Label,
)
