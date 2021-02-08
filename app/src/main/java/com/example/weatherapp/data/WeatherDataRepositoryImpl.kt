package com.example.weatherapp.data

class WeatherDataRepositoryImpl(private val weatherRemoteDataSource: WeatherRemoteDataSource): WeatherDataRepository {

    override suspend fun getWeatherByCityName(
        cityName: String,
        appId: String,
        units: String
    ): WeatherData {
        return weatherRemoteDataSource.getWeatherByCityName(cityName, appId, units)
    }

}