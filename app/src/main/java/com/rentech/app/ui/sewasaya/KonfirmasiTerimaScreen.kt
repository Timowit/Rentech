package com.rentech.app.ui.sewasaya

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KotakCentang
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.sewa.DraftSewa
import com.rentech.app.ui.sewa.KartuBarangSewa
import com.rentech.app.ui.sewa.KerangkaSewa
import com.rentech.app.ui.sewa.rentangLengkap
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary

@Composable
fun KonfirmasiTerimaScreen(draft: DraftSewa, onKembali: () -> Unit, onKonfirmasi: () -> Unit) {
    val b = draft.barang
    val periksa = listOf(
        "Kondisi fisik sesuai foto di aplikasi",
        "Kelengkapan sesuai (${b.kelengkapan.replaceFirstChar { it.lowercase() }})",
        "Menyala dan berfungsi normal saat diuji",
    )
    val tercentang = remember { mutableStateListOf(false, false, false) }
    var fotoKelengkapan by rememberSaveable { mutableStateOf<String?>(null) }
    val pilihFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) fotoKelengkapan = uri.toString()
    }

    KerangkaSewa(
        judul = "Serah terima barang",
        onKembali = onKembali,
        aksi = {
            Tombol(
                "Konfirmasi diterima",
                onClick = onKonfirmasi,
                modifier = Modifier.fillMaxWidth(),
                aktif = tercentang.all { it },
            )
        },
    ) {
        KartuBarangSewa(b) { Text(rentangLengkap(draft), style = Teks.Keterangan, color = TextSecondary) }
        Text(
            "Cek barang sebelum menerima. Dana diteruskan ke pemilik setelah kamu mengonfirmasi.",
            style = Teks.Isi,
            color = TextSecondary,
        )
        Column(Modifier.fillMaxWidth().kartu().padding(horizontal = 21.dp, vertical = 13.dp)) {
            Text("Periksa kondisi", style = Teks.IsiTebal, color = TextPrimary)
            periksa.forEachIndexed { i, teks ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { tercentang[i] = !tercentang[i] }
                        .padding(top = 13.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    KotakCentang(tercentang[i], ukuran = 24.dp, radius = 7.dp)
                    Text(
                        teks,
                        style = Teks.Isi,
                        color = if (tercentang[i]) TextPrimary else TextSecondary,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Bukti foto", style = Teks.Label, color = TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                FotoBukti(b.foto, "Foto kondisi", Modifier.weight(1f))
                val bentuk = RoundedCornerShape(18.dp)
                val ada = fotoKelengkapan != null
                Column(
                    Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(bentuk)
                        .background(if (ada) SuccessContainer else PrimaryContainer)
                        .border(1.5.dp, if (ada) Success else Primary, bentuk)
                        .clickable {
                            pilihFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Ikon(
                        if (ada) R.drawable.ikon_check_circle_fill else R.drawable.ikon_add_a_photo,
                        if (ada) Success else Primary,
                        ukuran = 22.dp,
                    )
                    Text("Foto kelengkapan", style = Teks.Label, color = if (ada) Success else Primary)
                }
            }
        }
    }
}

@Composable
private fun FotoBukti(foto: Int, label: String, modifier: Modifier) {
    Box(modifier.height(110.dp).clip(RoundedCornerShape(18.dp))) {
        Image(
            painterResource(foto),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color(0x8C121726))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Ikon(R.drawable.ikon_check_circle_fill, OnPrimary, ukuran = 14.dp)
            Text(label, style = Teks.Keterangan, color = OnPrimary)
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun KonfirmasiTerimaPreview() {
    RentechTheme { KonfirmasiTerimaScreen(DraftSewa(), {}, {}) }
}
