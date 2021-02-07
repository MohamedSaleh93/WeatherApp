package com.example.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.state.*
import com.google.android.gms.location.LocationServices

class MainViewModel(private val activity: Activity): ViewModel() {

    private val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    val permissionState = MutableLiveData<PermissionState>()
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    private var geoCoder = Geocoder(activity)
    val cityNameObservable = MutableLiveData<String>()

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
}