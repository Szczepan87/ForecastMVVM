package com.example.forecastmvvm.data.provider

import com.example.forecastmvvm.data.db.entity.WeatherLocation
import com.example.forecastmvvm.internal.DEFAULT_LOCATION

class LocationProviderImpl : LocationProvider {
    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        return true
    }

    override suspend fun getPreferredLocationString(): String {
        return DEFAULT_LOCATION
    }
}