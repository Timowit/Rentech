package com.rentech.app.ui.pemilik

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.BilahAksi
import com.rentech.app.ui.komponen.BilahAtas
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.IndikatorLangkah
import com.rentech.app.ui.komponen.KolomIsian
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Background
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Surface
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary

private val pilihanKondisi = listOf("Baru", "Sangat baik", "Baik")
private const val BATAS_DESKRIPSI = 500

/** Kerangka dua langkah "Tambah barang": bilah atas, isi bergulir, dan bilah aksi di bawah. */
@Composable
private fun KerangkaTambahBarang(
    judul: String,
    langkah: Int,
    teksTombol: String,
    onKembali: () -> Unit,
    onLanjut: () -> Unit,
    isi: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Background).imePadding()) {
        BilahAtas(judul, onKembali)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp)),
            verticalArrangement = Arrangement.spacedBy(34.dp),
        ) {
            IndikatorLangkah(langkah = langkah, total = 2)
            isi()
        }
        BilahAksi { Tombol(teksTombol, onClick = onLanjut, modifier = Modifier.fillMaxWidth()) }
    }
}

@Composable
fun TambahBarangScreen(onKembali: () -> Unit, onLanjut: () -> Unit) {
    var nama by rememberSaveable { mutableStateOf("") }
    var kategori by rememberSaveable { mutableStateOf("") }
    var kondisi by rememberSaveable { mutableStateOf(1) }
    var deskripsi by rememberSaveable { mutableStateOf("") }

    KerangkaTambahBarang("Tambah barang", 1, "Lanjut ke harga", onKembali, onLanjut) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Ceritakan barangmu", style = Teks.Judul, color = TextPrimary)
            Text(
                "Foto yang jelas dan deskripsi jujur membantu penyewa memutuskan.",
                style = Teks.Isi,
                color = TextSecondary,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Foto barang", style = Teks.Label, color = TextPrimary)
                Text("1/6", style = Teks.Keterangan, color = TextTertiary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Box(Modifier.size(114.dp).clip(RoundedCornerShape(18.dp))) {
                    Image(
                        painterResource(R.drawable.foto_drone),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Text(
                        "Sampul",
                        style = Teks.Keterangan,
                        color = OnPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                SlotFoto(utama = true)
                SlotFoto(utama = false)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
            KolomIsian("Nama barang", nama, { nama = it }, "Drone lipat 4K")
            KolomIsian("Kategori", kategori, { kategori = it }, "Drone")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Kondisi", style = Teks.Label, color = TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pilihanKondisi.forEachIndexed { i, label ->
                        Chip(
                            label,
                            terpilih = i == kondisi,
                            onClick = { kondisi = i },
                            padding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Deskripsi", style = Teks.Label, color = TextPrimary)
                BasicTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it.take(BATAS_DESKRIPSI) },
                    textStyle = Teks.Isi.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Primary),
                    decorationBox = { kolom ->
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .kartu(radius = 18.dp)
                                .padding(horizontal = 18.dp, vertical = 16.dp),
                        ) {
                            if (deskripsi.isEmpty()) {
                                Text(
                                    "Isi kelengkapan, kondisi, dan hal yang perlu diketahui penyewa.",
                                    style = Teks.Isi,
                                    color = TextTertiary,
                                )
                            }
                            kolom()
                        }
                    },
                )
                Text(
                    "${deskripsi.length}/$BATAS_DESKRIPSI",
                    style = Teks.Keterangan,
                    color = TextTertiary,
                    modifier = Modifier.align(Alignment.End),
                )
            }
        }
    }
}

@Composable
private fun SlotFoto(utama: Boolean) {
    val bentuk = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .size(114.dp)
            .clip(bentuk)
            .background(if (utama) PrimaryContainer else Surface)
            .border(1.5.dp, if (utama) Primary else Outline, bentuk)
            .clickable { },
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Ikon(R.drawable.ikon_add_a_photo, if (utama) Primary else TextTertiary)
        if (utama) Text("Tambah foto", style = Teks.Label, color = Primary)
    }
}

@Composable
fun HargaKetersediaanScreen(onKembali: () -> Unit, onTerbitkan: () -> Unit) {
    var tersedia by rememberSaveable { mutableStateOf(true) }
    var serahLangsung by rememberSaveable { mutableStateOf(true) }

    KerangkaTambahBarang("Harga dan ketersediaan", 2, "Terbitkan barang", onKembali, onTerbitkan) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Atur harga dan deposit", style = Teks.Judul, color = TextPrimary)
            Text("Kamu bisa mengubahnya kapan saja dari halaman barang.", style = Teks.Isi, color = TextSecondary)
        }

        Column(
            Modifier.fillMaxWidth().kartu().padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Text("Harga per hari", style = Teks.Label, color = TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.Bottom) {
                Text("Rp200.000", style = Teks.Display, color = TextPrimary, modifier = Modifier.alignByBaseline())
                Text("/hari", style = Teks.Isi, color = TextTertiary, modifier = Modifier.alignByBaseline())
            }
            Pemisah()
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Deposit", style = Teks.Label, color = TextSecondary)
                    Text("Rp3.000.000", style = Teks.JudulBilah, color = TextPrimary)
                }
                Ikon(R.drawable.ikon_edit_note, Primary, Modifier.clickable { }, ukuran = 22.dp)
            }
        }

        Column(Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 8.dp)) {
            BarisSakelar("Tersedia untuk disewa", "Kamu bisa mematikannya kapan saja", tersedia) { tersedia = it }
            BarisSakelar("Serah terima langsung", "Penyewa mengambil di lokasimu", serahLangsung) { serahLangsung = it }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(21.dp))
                .background(PrimaryContainer)
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Ikon(R.drawable.ikon_account_balance_wallet, Primary, ukuran = 22.dp)
            Text(
                "Deposit dikembalikan kepada penyewa setelah barang kembali sesuai kondisi.",
                style = Teks.Isi,
                color = OnPrimaryContainer,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BarisSakelar(judul: String, keterangan: String, nyala: Boolean, onUbah: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(judul, style = Teks.IsiTebal, color = TextPrimary)
            Text(keterangan, style = Teks.Keterangan, color = TextSecondary)
        }
        Switch(
            checked = nyala,
            onCheckedChange = onUbah,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Primary,
                checkedThumbColor = Surface,
                checkedBorderColor = Primary,
                uncheckedTrackColor = Outline,
                uncheckedThumbColor = Surface,
                uncheckedBorderColor = Outline,
            ),
        )
    }
}

@Preview(heightDp = 986, widthDp = 412)
@Composable
private fun TambahBarangPreview() {
    RentechTheme { TambahBarangScreen({}, {}) }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun HargaPreview() {
    RentechTheme { HargaKetersediaanScreen({}, {}) }
}
