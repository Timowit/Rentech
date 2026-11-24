package com.rentech.app.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rentech.app.R
import com.rentech.app.ui.theme.Inter
import com.rentech.app.ui.theme.OnPrimary
import com.rentech.app.ui.theme.Primary
import com.rentech.app.ui.theme.RentechTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSelesai: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onSelesai()
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Primary),
        verticalArrangement = Arrangement.spacedBy(21.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painterResource(R.drawable.logo_splash),
            contentDescription = "Rentech",
        )
        Text(
            "Sewa perangkat, tanpa perlu beli",
            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 15.sp),
            color = OnPrimary,
        )
    }
}

@Preview
@Composable
private fun SplashPreview() {
    RentechTheme { SplashScreen(onSelesai = {}) }
}
