package com.ojolfund.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ojolfund.app.ui.OjolFundApp
import com.ojolfund.app.ui.theme.OjolfundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OjolfundTheme {
                OjolFundApp()
            }
        }
    }
}
