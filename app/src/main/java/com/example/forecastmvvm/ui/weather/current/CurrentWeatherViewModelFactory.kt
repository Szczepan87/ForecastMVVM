package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.data.repository.ForecastRepository

/**
 * Custom ViewModelFactory with injected ForecastRepository and UnitProvider.
 */
@Suppress("UNCHECKED_CAST")
class CurrentWeatherViewModelFactory(
        private val forecastRepository: ForecastRepository,
        private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CurrentWeatherViewModel(forecastRepository, unitProvider) as T
    }
}