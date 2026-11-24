package com.rentech.app.ui.sewa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rentech.app.R
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.Chip
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.Inter
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

@Composable
fun SerahTerimaScreen(draft: DraftSewa, onKembali: () -> Unit, onLanjut: () -> Unit) {
    KerangkaSewa(
        judul = "Serah terima",
        onKembali = onKembali,
        aksi = { Tombol("Lanjut ke ringkasan", onClick = onLanjut, modifier = Modifier.fillMaxWidth()) },
    ) {
        Text("Bagaimana barang sampai ke kamu?", style = Teks.Judul, color = TextPrimary)
        KartuOpsi(
            R.drawable.ikon_storefront,
            "Ambil sendiri",
            "Temui pemilik di lokasinya",
            terpilih = !draft.diantar,
            onClick = { draft.diantar = false },
            kanan = "Gratis",
        )
        KartuOpsi(
            R.drawable.ikon_handshake,
            "Diantar pemilik",
            "Pemilik mengantar ke alamatmu",
            terpilih = draft.diantar,
            onClick = { draft.diantar = true },
            kanan = rupiah(BIAYA_ANTAR),
        )
        PetaPerkiraan()
        Row(
            Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Ikon(R.drawable.ikon_location_on, Primary, ukuran = 22.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Area Kemang, ${draft.barang.lokasi}", style = Teks.IsiTebal, color = TextPrimary)
                Text(
                    "Alamat lengkap dibagikan setelah pemilik menerima pesanan.",
                    style = Teks.Keterangan,
                    color = TextSecondary,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            val jenis = if (draft.diantar) "pengantaran" else "pengambilan"
            val hari = draft.mulai?.let { " · ${tanggalPendek(it)}" }.orEmpty()
            Text("Waktu $jenis$hari", style = Teks.IsiTebal, color = TextPrimary)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slotWaktu.forEachIndexed { i, s -> Chip(s, terpilih = i == draft.slot, onClick = { draft.slot = i }) }
            }
        }
    }
}

/** Peta area perkiraan. Lokasi persis baru dibagikan setelah pesanan diterima. */
@Composable
private fun PetaPerkiraan() {
    val bentuk = RoundedCornerShape(21.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(bentuk)
            .border(1.dp, Outline, bentuk),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painterResource(R.drawable.peta_kemang),
            contentDescription = "Peta area Kemang",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0x29000000))
                .border(1.5.dp, Primary, CircleShape)
        )
        Ikon(R.drawable.ikon_location_on, Primary, ukuran = 36.dp)
        Text(
            "© OpenStreetMap contributors",
            style = Teks.Keterangan.copy(fontFamily = Inter, fontSize = 9.sp),
            color = TextSecondary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xD9FFFFFF))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun SerahTerimaPreview() {
    RentechTheme { SerahTerimaScreen(DraftSewa(), {}, {}) }
}
