package com.brkckr.liquidorb

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.brkckr.liquidorb.ui.screen.OrbEditorScreen
import com.brkckr.liquidorb.ui.theme.LiquidOrbTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiquidOrbTheme {
                OrbEditorScreen()
            }
        }
    }
}
