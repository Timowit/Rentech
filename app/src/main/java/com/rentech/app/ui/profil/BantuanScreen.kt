package com.rentech.app.ui.profil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.NavigasiBawah
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.TabPenyewa
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private data class Tanya(val pertanyaan: String, val jawaban: String)

// Jawaban mengikuti alur yang tertulis di README.
private val pertanyaanUmum = listOf(
    Tanya(
        "Bagaimana dana saya dilindungi?",
        "Pembayaran lewat transfer ditahan Rentech dan baru diteruskan ke pemilik setelah kamu mengonfirmasi barang sudah diterima.",
    ),
    Tanya(
        "Apa yang terjadi jika barang rusak?",
        "Kondisi barang difoto saat serah terima dan saat pengembalian. Foto itu jadi bukti kalau terjadi sengketa, dan tim kami membantu menyelesaikannya.",
    ),
    Tanya(
        "Kapan deposit dikembalikan?",
        "Deposit kembali penuh setelah barang dikembalikan dalam kondisi baik.",
    ),
    Tanya(
        "Bagaimana cara mengganti peran?",
        "Buka Profil, lalu pilih Penyewa atau Pemilik di bagian Peran aktif.",
    ),
)

@Composable
fun BantuanScreen(onKembali: () -> Unit, onMulaiPercakapan: () -> Unit, onPilihTab: (Int) -> Unit) {
    var cari by rememberSaveable { mutableStateOf("") }
    var terbuka by rememberSaveable { mutableIntStateOf(-1) }
    val hasil = pertanyaanUmum.filter { it.pertanyaan.contains(cari.trim(), ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(Background)) {
        BilahAtas("Bantuan", onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(21.dp))
                    .background(Primary)
                    .padding(21.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
            ) {
                WadahIkon(R.drawable.ikon_support_agent, Primary, Surface, ukuranIkon = 24.dp)
                Text("Hubungi tim Rentech", style = Teks.Judul, color = OnPrimary)
                Text(
                    "Ada masalah selama sewa? Tim kami membantu langsung, bukan hanya lewat formulir.",
                    style = Teks.Isi,
                    color = OnPrimary,
                )
                Row(
                    Modifier
                        .clip(RoundedCornerShape(21.dp))
                        .background(Surface)
                        .clickable(onClick = onMulaiPercakapan)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Ikon(R.drawable.ikon_chat, Primary, ukuran = 18.dp)
                    Text("Mulai percakapan", style = Teks.IsiTebal, color = Primary)
                }
            }

            BasicTextField(
                value = cari,
                onValueChange = { cari = it },
                singleLine = true,
                textStyle = Teks.Isi.copy(color = TextPrimary),
                cursorBrush = SolidColor(Primary),
                decorationBox = { kolom ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .kartu(radius = 18.dp)
                            .padding(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Ikon(R.drawable.ikon_search, TextTertiary, ukuran = 22.dp)
                        Box(Modifier.weight(1f)) {
                            if (cari.isEmpty()) Text("Cari pertanyaan", style = Teks.Isi, color = TextTertiary)
                            kolom()
                        }
                    }
                },
            )

            Text("Pertanyaan umum", style = Teks.JudulBilah, color = TextPrimary)

            Column(Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 5.dp)) {
                if (hasil.isEmpty()) {
                    Text(
                        "Pertanyaan tidak ditemukan. Coba kata lain atau hubungi tim kami.",
                        style = Teks.Isi,
                        color = TextTertiary,
                        modifier = Modifier.padding(vertical = 18.dp),
                    )
                }
                hasil.forEachIndexed { i, t ->
                    if (i > 0) Pemisah()
                    val indeks = pertanyaanUmum.indexOf(t)
                    BarisTanya(t, terbuka = terbuka == indeks) {
                        terbuka = if (terbuka == indeks) -1 else indeks
                    }
                }
            }
        }
        NavigasiBawah(TabPenyewa, terpilih = 3, onPilih = onPilihTab)
    }
}

@Composable
private fun BarisTanya(t: Tanya, terbuka: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t.pertanyaan, style = Teks.IsiTebal, color = TextPrimary, modifier = Modifier.weight(1f))
            Ikon(
                R.drawable.ikon_add,
                TextTertiary,
                Modifier.rotate(if (terbuka) 45f else 0f),
                ukuran = 20.dp,
            )
        }
        AnimatedVisibility(terbuka) {
            Text(t.jawaban, style = Teks.Isi, color = TextSecondary)
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun BantuanPreview() {
    RentechTheme { BantuanScreen({}, {}, {}) }
}
