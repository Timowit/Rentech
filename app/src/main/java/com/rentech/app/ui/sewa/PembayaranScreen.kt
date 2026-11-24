package com.rentech.app.ui.sewa

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.WadahIkon
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import com.rentech.app.ui.theme.Warning
import com.rentech.app.ui.theme.WarningContainer
import kotlinx.coroutines.delay

/** Tiga digit terakhir jumlah transfer, supaya tim cepat mencocokkan transferan. */
private const val KODE_UNIK = 123L
private const val BATAS_TRANSFER_DETIK = 2 * 60 * 60L

@Composable
fun MetodePembayaranScreen(draft: DraftSewa, onKembali: () -> Unit, onLanjut: () -> Unit) {
    KerangkaSewa(
        judul = "Metode pembayaran",
        onKembali = onKembali,
        aksi = { AksiDenganRingkasan("Total", rupiah(draft.total), "Lanjut", onClick = onLanjut) },
    ) {
        KartuOpsi(
            R.drawable.ikon_account_balance_wallet,
            "Transfer bank ke Rentech",
            "Bayar sekarang, unggah bukti transfer. Dana dan deposit ditahan Rentech sampai sewa selesai.",
            terpilih = !draft.tunai,
            onClick = { draft.tunai = false },
            tengahVertikal = false,
        ) { LencanaKecil("Terlindungi", Success, SuccessContainer) }
        KartuOpsi(
            R.drawable.ikon_handshake,
            "Bayar tunai saat serah terima",
            "Bayar sewa dan deposit langsung ke pemilik ketika bertemu dan barang sudah dicek.",
            terpilih = draft.tunai,
            onClick = { draft.tunai = true },
            tengahVertikal = false,
        ) { LencanaKecil("Tanpa dana ditahan", Warning, WarningContainer) }
        KotakInfo(
            R.drawable.ikon_info,
            "Kalau memilih tunai: Rentech tidak memegang uangmu, jadi pengembalian dana dan klaim kerusakan " +
                "bergantung pada kesepakatan kamu dan pemilik. Keduanya tetap wajib mengonfirmasi uang diterima " +
                "di aplikasi supaya ada catatan.",
            warna = Warning,
            latar = WarningContainer,
        )
        RincianBiaya(
            draft,
            judul = null,
            labelDeposit = "Deposit (kembali setelah sewa)",
            labelTotal = "Total dibayar",
            totalMenonjol = false,
            labelSewa = "Sewa ${draft.hari} hari",
        )
    }
}

@Composable
private fun LencanaKecil(teks: String, warna: Color, latar: Color) {
    Text(
        teks,
        style = Teks.Label,
        color = warna,
        modifier = Modifier
            .padding(top = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(latar)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
fun TransferScreen(draft: DraftSewa, onKembali: () -> Unit, onKirim: () -> Unit) {
    var sisa by rememberSaveable { mutableLongStateOf(BATAS_TRANSFER_DETIK) }
    LaunchedEffect(Unit) {
        while (sisa > 0) {
            delay(1000)
            sisa--
        }
    }
    var bukti by rememberSaveable { mutableStateOf<String?>(null) }
    val pilihFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) bukti = uri.toString()
    }
    val jumlah = draft.total + KODE_UNIK

    KerangkaSewa(
        judul = "Transfer ke Rentech",
        onKembali = onKembali,
        aksi = {
            Tombol("Kirim bukti transfer", onClick = onKirim, modifier = Modifier.fillMaxWidth(), aktif = bukti != null)
        },
    ) {
        KotakInfo(
            R.drawable.ikon_schedule,
            "Sisa waktu %02d:%02d:%02d".format(sisa / 3600, sisa % 3600 / 60, sisa % 60),
            warna = Warning,
            latar = WarningContainer,
            judul = "Transfer dan unggah bukti dalam 2 jam",
            ukuranIkon = 22.dp,
        )
        Column(
            Modifier.fillMaxWidth().kartu().padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            BarisRekening("Bank", "BCA")
            BarisRekening("Nomor rekening", "123 456 7890", besar = true, salin = "1234567890")
            BarisRekening("Atas nama", "PT Rentech Indonesia (contoh)")
            BarisRekening(
                "Jumlah transfer, tepat sampai 3 digit terakhir",
                rupiah(jumlah),
                besar = true,
                salin = jumlah.toString(),
            )
            Text(
                "Tiga digit terakhir ($KODE_UNIK) adalah kode unik supaya tim kami cepat mencocokkan transferanmu.",
                style = Teks.Keterangan,
                color = TextSecondary,
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .kartu(garis = Primary, tebalGaris = 1.5.dp)
                .clickable { pilihFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                .padding(vertical = 21.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val ada = bukti != null
            WadahIkon(
                if (ada) R.drawable.ikon_check_circle_fill else R.drawable.ikon_upload,
                if (ada) Success else Primary,
                if (ada) SuccessContainer else PrimaryContainer,
                ukuran = 48.dp,
                radius = 16.dp,
            )
            Text(if (ada) "Bukti transfer terpilih" else "Unggah bukti transfer", style = Teks.IsiTebal, color = if (ada) Success else Primary)
            Text(
                if (ada) "Ketuk untuk mengganti foto" else "Foto atau tangkapan layar, maks. 5 MB",
                style = Teks.Keterangan,
                color = TextTertiary,
            )
        }
        KotakInfo(
            R.drawable.ikon_verified_user_fill,
            "Tim Rentech memeriksa bukti transfer, biasanya kurang dari 30 menit pada jam 08.00–22.00. " +
                "Setelah itu pesanan diteruskan ke pemilik.",
            warna = OnPrimaryContainer,
            latar = PrimaryContainer,
            warnaIkon = Primary,
        )
    }
}

@Composable
private fun BarisRekening(label: String, nilai: String, besar: Boolean = false, salin: String? = null) {
    @Suppress("DEPRECATION")
    val papanKlip = LocalClipboardManager.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(label, style = Teks.Keterangan, color = TextTertiary)
            Text(nilai, style = if (besar) Teks.JudulBilah else Teks.IsiTebal, color = TextPrimary)
        }
        if (salin != null) {
            var tersalin by rememberSaveable { mutableStateOf(false) }
            Text(
                if (tersalin) "Tersalin" else "Salin",
                style = Teks.Label,
                color = Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(13.dp))
                    .background(PrimaryContainer)
                    .clickable {
                        papanKlip.setText(AnnotatedString(salin))
                        tersalin = true
                    }
                    .padding(horizontal = 13.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun MetodePreview() {
    RentechTheme { MetodePembayaranScreen(DraftSewa(), {}, {}) }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun TransferPreview() {
    RentechTheme { TransferScreen(DraftSewa(), {}, {}) }
}
