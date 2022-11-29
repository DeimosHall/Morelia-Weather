package com.deimos.moreliaclima

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class WeatherResponse(
    @SerializedName("main") var main: MainWeather,
    @SerializedName("name") var name: String,
    @SerializedName("cod") var cod: Int
)