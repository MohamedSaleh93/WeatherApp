package com.example.weatherapp.state

sealed class PermissionState

object PermissionGrantedState: PermissionState()

object PermissionNotGrantedState: PermissionState()

object ShouldShowRequestPermissionRationaleState: PermissionState()