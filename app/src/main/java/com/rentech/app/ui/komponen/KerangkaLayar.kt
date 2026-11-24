package com.rentech.app.ui.komponen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rentech.app.ui.theme.Background

/**
 * Kerangka layar biasa: bilah atas (opsional), isi yang bisa digulir,
 * dan area tombol yang menempel di bawah.
 */
@Composable
fun KerangkaLayar(
    modifier: Modifier = Modifier,
    bilahAtas: (@Composable () -> Unit)? = null,
    paddingIsi: PaddingValues = PaddingValues(start = 21.dp, end = 21.dp, top = 8.dp, bottom = 21.dp),
    jarakIsi: Dp = 34.dp,
    rataTengah: Boolean = false,
    bawah: (@Composable ColumnScope.() -> Unit)? = null,
    isi: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(Background)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        if (bilahAtas != null) bilahAtas() else Spacer(Modifier.statusBarsPadding())
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(paddingIsi),
            verticalArrangement = Arrangement.spacedBy(jarakIsi),
            horizontalAlignment = if (rataTengah) Alignment.CenterHorizontally else Alignment.Start,
            content = isi,
        )
        if (bawah != null) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 21.dp, end = 21.dp, bottom = 34.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = bawah,
            )
        }
    }
}
