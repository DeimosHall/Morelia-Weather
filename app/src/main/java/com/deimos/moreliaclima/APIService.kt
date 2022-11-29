package com.deimos.moreliaclima

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getTemperature(@Url url: String): Response<WeatherResponse>
}