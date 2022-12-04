package com.deimos.moreliaclima.device

import android.content.Context
import android.content.res.Configuration

class Device(private val context: Context) {

    fun isDarkModeEnabled(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}