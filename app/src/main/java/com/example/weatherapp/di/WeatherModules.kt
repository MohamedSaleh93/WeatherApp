package com.example.weatherapp.di

import android.app.Activity
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.*
import com.example.weatherapp.ui.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else {
    OkHttpClient.Builder().build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .build()
}

private fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
    return retrofit.create(WeatherApiService::class.java)
}

val applicationModule = module {
    viewModel { (activity: Activity) -> MainViewModel(activity, get()) }

    single {
        provideOkHttpClient()
    }

    single { provideRetrofit(get(), BuildConfig.BASE_URL) }

    single { provideWeatherApiService(get()) }

    single<WeatherRemoteDataSource> { WeatherRemoteDataSourceImpl(get()) }

    single<WeatherDataRepository> { WeatherDataRepositoryImpl(get()) }
}