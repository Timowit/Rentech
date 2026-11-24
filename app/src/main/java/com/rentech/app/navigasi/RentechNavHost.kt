package com.rentech.app.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.onboarding.SplashScreen

object Rute {
    const val SPLASH = "splash"
}

@Composable
fun RentechNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rute.SPLASH) {
        composable(Rute.SPLASH) {
            SplashScreen(onSelesai = { })
        }
    }
}
