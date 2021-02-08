package com.example.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.WeatherDataRepository
import com.example.weatherapp.state.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.UnknownHostException

class MainViewModel(private val activity: Activity,
                    private val weatherDataRepository: WeatherDataRepository): ViewModel() {

    private val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val TEMP_UNIT = "metric"
    val permissionState = MutableLiveData<PermissionState>()
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private var geoCoder = Geocoder(activity)
    val cityNameObservable = MutableLiveData<String>()
    val weatherDataResponseState = MutableLiveData<WeatherResponseState>()

    init {
        checkAndRequestLocationPermission()
    }

    private fun checkAndRequestLocationPermission() {
        when {
            checkLocationPermission() == PackageManager.PERMISSION_GRANTED -> {
                permissionState.postValue(PermissionGrantedState)
            }
            shouldShowRequestPermissionRationale(activity, LOCATION_PERMISSION) -> {
                permissionState.postValue(ShouldShowRequestPermissionRationaleState)
            }
            else -> {
                permissionState.postValue(PermissionNotGrantedState)
            }
        }
    }

    private fun checkLocationPermission(): Int {
        return ContextCompat.checkSelfPermission(activity,
            LOCATION_PERMISSION)
    }

    @SuppressLint("MissingPermission")
    fun getUserCityName() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                getCityName(it.latitude, it.longitude)
            }
        }
    }

    private fun getCityName(lat: Double, long: Double) {
        val addresses = geoCoder.getFromLocation(lat, long, 1)
        cityNameObservable.postValue(addresses[0].adminArea)
    }

    fun loadWeatherByCityName(cityName: String) {
        weatherDataResponseState.postValue(LoadingWeatherData)
        viewModelScope.launch {
            try {
                val weatherData = weatherDataRepository.getWeatherByCityName(
                    cityName,
                    BuildConfig.APP_ID, TEMP_UNIT
                )
                weatherDataResponseState.postValue(WeatherDataLoaded(weatherData.mainWeatherData.temp, cityName))
            } catch (e: Exception) {
                weatherDataResponseState.postValue(WeatherDataError)
            }
        }
    }
}