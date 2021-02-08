package com.example.weatherapp.data

interface WeatherRemoteDataSource {

    suspend fun getWeatherByCityName(cityName: String,
                                     appId: String,
                                     units: String): WeatherData
}