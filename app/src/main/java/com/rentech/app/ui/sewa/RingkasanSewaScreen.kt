package com.rentech.app.ui.sewa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.BarisRincian
import com.rentech.app.ui.komponen.Ikon
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.Lencana
import com.rentech.app.ui.komponen.Pemisah
import com.rentech.app.ui.komponen.Tombol
import com.rentech.app.ui.komponen.kartu
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.Success
import com.rentech.app.ui.theme.SuccessContainer
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextTertiary
import java.time.LocalDate

@Composable
fun RingkasanSewaScreen(draft: DraftSewa, onKembali: () -> Unit, onBayar: () -> Unit) {
    val b = draft.barang
    KerangkaSewa(
        judul = "Ringkasan sewa",
        onKembali = onKembali,
        aksi = {
            Tombol("Bayar dan tahan dana", onClick = onBayar, modifier = Modifier.fillMaxWidth())
            Text(
                "Pembayaran tidak diteruskan ke pemilik sebelum barang kamu terima.",
                style = Teks.Keterangan,
                color = TextTertiary,
            )
        },
    ) {
        KartuBarangSewa(b) { Lencana("Pemilik terverifikasi", Success, SuccessContainer) }

        Column(
            Modifier.fillMaxWidth().kartu().padding(21.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Tanggal sewa", style = Teks.IsiTebal, color = TextPrimary)
                Lencana("${draft.hari} hari", OnPrimaryContainer, PrimaryContainer)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                KotakTanggal("Mulai", draft.mulai, Modifier.weight(1f))
                KotakTanggal("Selesai", draft.selesai, Modifier.weight(1f))
            }
        }

        RincianBiaya(draft, judul = "Rincian biaya", labelDeposit = "Deposit (dikembalikan setelah sewa)", labelTotal = "Total dibayar sekarang")

        KotakInfo(
            R.drawable.ikon_verified_user_fill,
            "Pembayaran baru diteruskan ke pemilik setelah barang kamu terima. Deposit dikembalikan setelah barang kembali.",
            warna = OnPrimaryContainer,
            latar = PrimaryContainer,
            judul = "Dana kamu ditahan",
            warnaIkon = Primary,
            ukuranIkon = 24.dp,
            radius = 21.dp,
        )
    }
}

@Composable
private fun KotakTanggal(label: String, t: LocalDate?, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(13.dp))
            .background(SurfaceVariant)
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ikon(R.drawable.ikon_event, Primary, ukuran = 20.dp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = Teks.Keterangan, color = TextTertiary)
            Text(t?.let { tanggalLengkap(it) } ?: "-", style = Teks.Label, color = TextPrimary)
        }
    }
}

/** Kartu rincian biaya sewa, layanan, antar, deposit, dan total. */
@Composable
fun RincianBiaya(
    draft: DraftSewa,
    judul: String?,
    labelDeposit: String,
    labelTotal: String,
    totalMenonjol: Boolean = true,
    labelSewa: String = "Sewa ${draft.hari} hari × ${rupiah(draft.barang.hargaPerHari)}",
) {
    Column(
        Modifier.fillMaxWidth().kartu().padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(if (judul != null) 13.dp else 8.dp),
    ) {
        if (judul != null) Text(judul, style = Teks.IsiTebal, color = TextPrimary)
        BarisRincian(labelSewa, rupiah(draft.biayaSewa))
        BarisRincian("Biaya layanan", rupiah(BIAYA_LAYANAN))
        if (draft.diantar) BarisRincian("Biaya antar", rupiah(draft.biayaAntar))
        BarisRincian(labelDeposit, rupiah(draft.barang.deposit))
        Pemisah()
        BarisRincian(
            labelTotal,
            rupiah(draft.total),
            tebal = true,
            gayaNilai = if (totalMenonjol) Teks.JudulBilah else Teks.IsiTebal,
            warnaNilai = if (totalMenonjol) Primary else TextPrimary,
        )
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun RingkasanPreview() {
    RentechTheme { RingkasanSewaScreen(DraftSewa(), {}, {}) }
}
