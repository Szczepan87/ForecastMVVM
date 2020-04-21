package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.data.repository.ForecastRepository
import com.example.forecastmvvm.internal.lazyDeffered

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {
    val unitSystem = unitProvider.unitProvider()

    val weather by lazyDeffered { forecastRepository.getCurrentWeather() }
}
