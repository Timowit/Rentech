package com.rentech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rentech.app.navigasi.RentechNavHost
import com.rentech.app.ui.theme.RentechTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentechTheme {
                RentechNavHost()
            }
        }
    }
}
