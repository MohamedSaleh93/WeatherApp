package com.example.weatherapp.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getWeatherByCityName(@Query("q") cityName: String,
                                     @Query("appid") appId: String,
                                     @Query("units") units: String): WeatherData
}