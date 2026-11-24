package com.rentech.app.ui.komponen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.Teks
import com.rentech.app.ui.theme.TextPrimary
import com.rentech.app.ui.theme.TextTertiary

@Composable
fun KolomIsian(
    label: String,
    nilai: String,
    onUbah: (String) -> Unit,
    petunjuk: String,
    modifier: Modifier = Modifier,
    keterangan: String? = null,
    sandi: Boolean = false,
    jenisKeyboard: KeyboardType = KeyboardType.Text,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = Teks.Label, color = TextPrimary)
        BasicTextField(
            value = nilai,
            onValueChange = onUbah,
            singleLine = true,
            textStyle = Teks.Isi.copy(color = TextPrimary),
            cursorBrush = SolidColor(Primary),
            visualTransformation = if (sandi) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (sandi) KeyboardType.Password else jenisKeyboard,
            ),
            decorationBox = { isi ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .kartu(radius = 18.dp)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (nilai.isEmpty()) Text(petunjuk, style = Teks.Isi, color = TextTertiary)
                    isi()
                }
            },
        )
        if (keterangan != null) Text(keterangan, style = Teks.Keterangan, color = TextTertiary)
    }
}
