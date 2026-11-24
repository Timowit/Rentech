package com.rentech.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SkemaTerang = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    error = Error,
    errorContainer = ErrorContainer,
)

@Composable
fun RentechTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SkemaTerang,
        typography = Typography,
        content = content,
    )
}
