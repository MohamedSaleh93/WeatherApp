package com.example.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.weatherapp.R
import com.example.weatherapp.state.PermissionGrantedState
import com.example.weatherapp.state.PermissionNotGrantedState
import com.example.weatherapp.state.PermissionState
import com.example.weatherapp.state.ShouldShowRequestPermissionRationaleState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

	private val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
	private val REQUEST_LOCATION_PERMISSION_CODE = 1001
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private lateinit var geocoder: Geocoder
	private val mainViewModel: MainViewModel by inject { parametersOf(this@MainActivity)}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		geocoder = Geocoder(this)

		mainViewModel.permissionState.observe(this, {
			permissionStateObserver(it)
		})
		mainViewModel.cityNameObservable.observe(this, {
			Toast.makeText(this, it, Toast.LENGTH_LONG).show()
		})
	}

	private fun permissionStateObserver(permissionState: PermissionState) {
		when(permissionState) {
			PermissionGrantedState -> {
				mainViewModel.getUserCityName()
			}
			ShouldShowRequestPermissionRationaleState -> {

			}
			PermissionNotGrantedState -> {
				requestPermissions(arrayOf(LOCATION_PERMISSION),
					REQUEST_LOCATION_PERMISSION_CODE)
			}
		}
	}

	private fun checkLocationPermission(): Int {
		return ContextCompat.checkSelfPermission(this,
			LOCATION_PERMISSION)
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode != REQUEST_LOCATION_PERMISSION_CODE) {
			return
		}
		if (checkLocationPermission() == PERMISSION_GRANTED) {
			mainViewModel.getUserCityName()
		} else {

		}
	}
}