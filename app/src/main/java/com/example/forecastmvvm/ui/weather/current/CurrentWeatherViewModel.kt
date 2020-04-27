package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.data.repository.ForecastRepository
import com.example.forecastmvvm.internal.lazyDeferred

/**
 * ViewModel for CurrentWeatherFragment that provides values initialized by lazyDeferred.
 * Unit system provided by UnitProvider. Constructor parameters injected.
 */

class CurrentWeatherViewModel(
        private val forecastRepository: ForecastRepository,
        unitProvider: UnitProvider
) : ViewModel() {
    val unitSystem = unitProvider.unitProvider()

    val weather by lazyDeferred { forecastRepository.getCurrentWeather() }

    val weatherLocation by lazyDeferred { forecastRepository.getWeatherLocation() }
}
