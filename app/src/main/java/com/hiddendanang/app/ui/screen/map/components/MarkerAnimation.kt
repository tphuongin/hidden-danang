package com.hiddendanang.app.ui.screen.map.components

import android.os.Handler
import android.os.Looper
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager

/**
 * Adds pulse animation to a marker
 * @param symbolManager The SymbolManager instance
 * @param symbol The symbol (marker) to animate
 * @param delayMs Delay before starting animation in milliseconds
 */
fun addPulseAnimation(
    symbolManager: SymbolManager,
    symbol: Symbol,
    delayMs: Long
) {
    val handler = Handler(Looper.getMainLooper())

    val pulseRunnable = object : Runnable {
        var isExpanded = false

        override fun run() {
            isExpanded = !isExpanded
            val newSize = if (isExpanded) 1.4f else 1.2f
            symbol.iconSize = newSize
            symbolManager.update(symbol)

            // Repeat animation every 800ms
            handler.postDelayed(this, 800)
        }
    }

    // Start animation after delay
    handler.postDelayed(pulseRunnable, delayMs)
}
