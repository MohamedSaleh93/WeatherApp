package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class MainWeatherData(val temp: Double)
data class WeatherData(
    @SerializedName("main")
    val mainWeatherData: MainWeatherData
)