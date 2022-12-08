package com.deimos.moreliaclima.device

import android.content.Context

class SharedPreferences(private val context: Context?) {
    private val SHARED_TEMP = "MyTempUnit"
    private val SHARED_TEMP_UNIT = "unit"
    private val DEFAULT_TEMP_UNIT = "celcius"

    private val sharedPreferences = context?.getSharedPreferences(SHARED_TEMP, Context.MODE_PRIVATE)
    private val editor = sharedPreferences?.edit()

    fun setSharedTemperatureUnit(unit: String) {
        editor?.putString(SHARED_TEMP_UNIT, unit)
        editor?.apply()
    }

    fun getSharedTemperatureUnit(): String? {
        return sharedPreferences?.getString(SHARED_TEMP_UNIT, DEFAULT_TEMP_UNIT)
    }
}