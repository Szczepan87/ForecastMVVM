package com.example.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecastmvvm.data.repository.ForecastRepository
import com.example.forecastmvvm.internal.METRIC_UNITS
import com.example.forecastmvvm.internal.lazyDeffered

class CurrentWeatherViewModel(private val forecastRepository: ForecastRepository) : ViewModel() {
    val unitSystem = METRIC_UNITS // get from settings

    val weather by lazyDeffered { forecastRepository.getCurrentWeather() }
    //lazy { viewModelScope.launch { forecastRepository.getCurrentWeather() } }

}
