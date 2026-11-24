package com.rentech.app.ui.sewa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rentech.app.R
import com.rentech.app.data.rupiah
import com.rentech.app.ui.komponen.KotakInfo
import com.rentech.app.ui.komponen.TombolBulat
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.OnPrimaryContainer
import com.rentech.app.ui.theme.Outline
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.PrimaryContainer
import com.rentech.app.ui.theme.RentechTheme
import com.rentech.app.ui.theme.SurfaceVariant
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextSecondary
import com.rentech.app.ui.theme.TextTertiary
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun PilihTanggalScreen(draft: DraftSewa, onKembali: () -> Unit, onLanjut: () -> Unit, hariIni: LocalDate = LocalDate.now()) {
    var bulanTampil by rememberSaveable { mutableStateOf(YearMonth.from(draft.mulai ?: hariIni).toString()) }
    val ym = YearMonth.parse(bulanTampil)

    KerangkaSewa(
        judul = "Pilih tanggal sewa",
        onKembali = onKembali,
        aksi = {
            AksiDenganRingkasan(
                label = ringkasRentang(draft),
                nilai = rupiah(draft.biayaSewa),
                teksTombol = "Lanjut",
                aktif = draft.hari >= 1,
                onClick = onLanjut,
            )
        },
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            TombolBulat(
                R.drawable.ikon_arrow_back,
                onClick = { if (ym > YearMonth.from(hariIni)) bulanTampil = ym.minusMonths(1).toString() },
                ukuran = 36.dp,
                ukuranIkon = 18.dp,
            )
            Text("${namaBulanPanjang[ym.monthValue - 1]} ${ym.year}", style = Teks.JudulBilah, color = TextPrimary)
            TombolBulat(
                R.drawable.ikon_arrow_forward,
                onClick = { bulanTampil = ym.plusMonths(1).toString() },
                ukuran = 36.dp,
                ukuranIkon = 18.dp,
            )
        }
        Kalender(ym, draft, hariIni)
        Row(horizontalArrangement = Arrangement.spacedBy(21.dp)) {
            Keterangan(Primary, "Dipilih")
            Keterangan(Outline, "Tidak tersedia")
        }
        KotakInfo(
            R.drawable.ikon_info,
            "Minimal sewa 1 hari. Hari dihitung dari pengambilan sampai pengembalian.",
            warna = TextSecondary,
            latar = SurfaceVariant,
        )
    }
}

@Composable
private fun Kalender(ym: YearMonth, draft: DraftSewa, hariIni: LocalDate) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(Modifier.fillMaxWidth()) {
            singkatanHari.forEach { h ->
                Text(
                    h,
                    style = Teks.Keterangan,
                    color = TextTertiary,
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
        val geser = ym.atDay(1).dayOfWeek.value - 1
        val sel = List(geser) { null } + (1..ym.lengthOfMonth()).map { ym.atDay(it) }
        sel.chunked(7).forEach { minggu ->
            Row(Modifier.fillMaxWidth()) {
                (0 until 7).forEach { i ->
                    SelTanggal(minggu.getOrNull(i), draft, hariIni, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SelTanggal(t: LocalDate?, draft: DraftSewa, hariIni: LocalDate, modifier: Modifier) {
    if (t == null) {
        Box(modifier.height(48.dp))
        return
    }
    val lewat = t.isBefore(hariIni)
    val terpakai = t in tanggalTidakTersedia
    val m = draft.mulai
    val s = draft.selesai
    val ujung = t == m || t == s
    val dalamRentang = m != null && s != null && !t.isBefore(m) && !t.isAfter(s)
    val warnaTeks = when {
        ujung -> OnPrimary
        dalamRentang -> OnPrimaryContainer
        terpakai -> Outline
        lewat -> TextTertiary
        else -> TextPrimary
    }
    Box(
        modifier
            .height(48.dp)
            .background(if (dalamRentang || (ujung && s != null)) PrimaryContainer else Color.Transparent)
            .clickable(enabled = !lewat && !terpakai) { draft.pilihTanggal(t) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (ujung) Primary else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Text("${t.dayOfMonth}", style = if (ujung) Teks.IsiTebal else Teks.Isi, color = warnaTeks)
        }
    }
}

@Composable
private fun Keterangan(warna: Color, label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(warna))
        Text(label, style = Teks.Keterangan, color = TextSecondary)
    }
}

@Preview(heightDp = 917, widthDp = 412)
@Composable
private fun PilihTanggalPreview() {
    RentechTheme { PilihTanggalScreen(DraftSewa(), {}, {}, hariIni = LocalDate.of(2026, 12, 9)) }
}
