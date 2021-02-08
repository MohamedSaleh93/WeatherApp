package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.state.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val REQUEST_LOCATION_PERMISSION_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private val mainViewModel: MainViewModel by inject { parametersOf(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this)

        mainViewModel.permissionState.observe(this, {
            permissionStateObserver(it)
        })
        mainViewModel.cityNameObservable.observe(this, {
            etCityName.setText(it)
            mainViewModel.loadWeatherByCityName(it)
        })
        mainViewModel.weatherDataResponseState.observe(this, {
            handleLoadingWeatherData(it)
        })

        etCityName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                var view: View? = currentFocus
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = View(this)
                }
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                mainViewModel.loadWeatherByCityName(etCityName.text.toString())
            }
            true
        }
    }

    private fun handleLoadingWeatherData(weatherResponseState: WeatherResponseState) {
        when (weatherResponseState) {
            LoadingWeatherData -> {
                showLoadingAndDisclaimer(getString(R.string.loading_weather))
            }
            is WeatherDataLoaded -> {
                loadingAndDisclaimer.visibility = View.GONE
                resultGroup.visibility = View.VISIBLE
                tvCityName.text = weatherResponseState.cityName
                tvCityTemp.text = "${weatherResponseState.temp}\u2103"
                changeDisclaimerByTemp(weatherResponseState.temp)
            }
            WeatherDataError -> {
                loadingProgressBar.visibility = View.GONE
                resultGroup.visibility = View.GONE
                tvDisclaimer.text = getString(R.string.something_went_wrong)
            }
        }
    }

    private fun changeDisclaimerByTemp(temp: Double) {
        tvDisclaimer.visibility = View.VISIBLE
        when {
            temp >= 30 -> {
                tvDisclaimer.text = getString(R.string.very_hot)
            }
            temp < 30 && temp >= 25 -> {
                tvDisclaimer.text = getString(R.string.sunny)
            }
            temp < 25 && temp >= 15 -> {
                tvDisclaimer.text = getString(R.string.good_temp)
            }
            temp < 15 -> {
                tvDisclaimer.text = getString(R.string.very_cold)
            }
        }
    }

    private fun permissionStateObserver(permissionState: PermissionState) {
        when (permissionState) {
            PermissionGrantedState -> {
                showLoadingAndDisclaimer(getString(R.string.getting_your_current_city_name))
                mainViewModel.getUserCityName()
            }
            ShouldShowRequestPermissionRationaleState -> {
                tvDisclaimer.text = getString(R.string.request_location_permission_disclaimer)
            }
            PermissionNotGrantedState -> {
                requestPermissions(
                    arrayOf(LOCATION_PERMISSION),
                    REQUEST_LOCATION_PERMISSION_CODE
                )
            }
        }
    }

    private fun checkLocationPermission(): Int {
        return ContextCompat.checkSelfPermission(
            this,
            LOCATION_PERMISSION
        )
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
            tvDisclaimer.text = getString(R.string.request_location_permission_disclaimer)
        }
    }

    private fun showLoadingAndDisclaimer(disclaimer: String) {
        resultGroup.visibility = View.GONE
        loadingAndDisclaimer.visibility = View.VISIBLE
        tvDisclaimer.text = disclaimer
    }
}