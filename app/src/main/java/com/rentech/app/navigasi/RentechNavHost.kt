package com.rentech.app.navigasi

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rentech.app.ui.onboarding.OnboardingScreen
import com.rentech.app.ui.onboarding.SplashScreen

object Rute {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
}

@Composable
fun RentechNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Rute.SPLASH) {
        composable(Rute.SPLASH) {
            SplashScreen(onSelesai = {
                nav.navigate(Rute.ONBOARDING) { popUpTo(Rute.SPLASH) { inclusive = true } }
            })
        }
        composable(Rute.ONBOARDING) {
            OnboardingScreen(onSelesai = { })
        }
    }
}
