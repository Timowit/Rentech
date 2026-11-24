package com.rentech.app.ui.sewasaya

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.sewa.KartuBarangSewa
import com.rentech.app.ui.sewa.KerangkaSewa
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning

private val labelNilai = listOf("Buruk", "Kurang", "Cukup", "Baik", "Sangat baik")
private val sorotan = listOf("Sesuai foto", "Serah terima lancar", "Pemilik responsif", "Barang bersih", "Tepat waktu")

@Composable
fun UlasanScreen(sewa: SewaSelesai, onKembali: () -> Unit, onKirim: () -> Unit) {
    var nilai by rememberSaveable { mutableIntStateOf(0) }
    val dipilih = remember { mutableStateListOf<String>() }
    var cerita by rememberSaveable { mutableStateOf("") }

    KerangkaSewa(
        judul = "Beri ulasan",
        onKembali = onKembali,
        aksi = { Tombol("Kirim ulasan", onClick = onKirim, modifier = Modifier.fillMaxWidth(), aktif = nilai > 0) },
    ) {
        KartuBarangSewa(sewa.barang) {
            Text(sewa.tanggal, style = Teks.Keterangan, color = TextSecondary)
            Lencana("Sewa selesai", Success, SuccessContainer)
        }
        Column(
            Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Bagaimana pengalamanmu?", style = Teks.JudulBilah, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { i ->
                    Ikon(
                        if (i <= nilai) R.drawable.ikon_star_fill else R.drawable.ikon_star,
                        if (i <= nilai) Warning else Outline,
                        Modifier.clickable { nilai = i },
                        ukuran = 40.dp,
                    )
                }
            }
            if (nilai > 0) Text(labelNilai[nilai - 1], style = Teks.IsiTebal, color = Warning)
            else Text("Ketuk bintang untuk menilai", style = Teks.Isi, color = TextTertiary)
        }
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            Text("Apa yang menonjol?", style = Teks.Label, color = TextPrimary)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sorotan.forEach { s ->
                    Chip(
                        s,
                        terpilih = s in dipilih,
                        onClick = { if (s in dipilih) dipilih.remove(s) else dipilih.add(s) },
                        padding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ceritakan pengalamanmu", style = Teks.Label, color = TextPrimary)
            BasicTextField(
                value = cerita,
                onValueChange = { cerita = it },
                textStyle = Teks.Isi.copy(color = TextPrimary),
                cursorBrush = SolidColor(Primary),
                decorationBox = { kolom ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .kartu(radius = 18.dp)
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                    ) {
                        if (cerita.isEmpty()) {
                            Text("Kondisi barang, proses serah terima, dan pemilik.", style = Teks.Isi, color = TextTertiary)
                        }
                        kolom()
                    }
                },
            )
        }
        KotakInfo(
            R.drawable.ikon_rate_review,
            "Penyewa dan pemilik sama-sama menilai. Ulasan membantu orang lain memilih dengan lebih yakin.",
            warna = TextSecondary,
            latar = SurfaceVariant,
        )
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun UlasanPreview() {
    RentechTheme { UlasanScreen(contohRiwayat.first(), {}, {}) }
}
