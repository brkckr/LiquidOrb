package com.brkckr.liquidorb.ui.util

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

/**
 * creates a color from a hex string.
 * supports both #RRGGBB and #AARRGGBB formats.
 */
fun colorFromHex(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return try {
        if (cleanHex.length == 6) {
            Color("#FF$cleanHex".toColorInt())
        } else {
            Color("#$cleanHex".toColorInt())
        }
    } catch (e: Exception) {
        Color.Black
    }
}


