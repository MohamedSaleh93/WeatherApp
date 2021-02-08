package com.example.weatherapp.data

class WeatherRemoteDataSourceImpl(private val weatherApiService: WeatherApiService) : WeatherRemoteDataSource {

    override suspend fun getWeatherByCityName(
        cityName: String,
        appId: String,
        units: String
    ): WeatherData {
        return weatherApiService.getWeatherByCityName(cityName, appId, units)
    }
}