package com.example.weatherapp.data

interface WeatherDataRepository {

    suspend fun getWeatherByCityName(cityName: String,
                                     appId: String,
                                     units: String): WeatherData
}