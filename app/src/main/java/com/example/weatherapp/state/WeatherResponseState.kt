package com.example.weatherapp.state

sealed class WeatherResponseState

object LoadingWeatherData: WeatherResponseState()

class WeatherDataLoaded(val temp: Double, val cityName: String): WeatherResponseState()

object WeatherDataError: WeatherResponseState()