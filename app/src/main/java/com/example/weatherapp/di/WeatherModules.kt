package com.example.weatherapp.di

import android.app.Activity
import com.example.weatherapp.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    viewModel { (activity: Activity) -> MainViewModel(activity) }
}