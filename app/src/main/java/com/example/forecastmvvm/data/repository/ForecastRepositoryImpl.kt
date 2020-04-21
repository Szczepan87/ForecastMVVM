package com.example.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.example.forecastmvvm.data.network.WeatherNetworkDataSource
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.internal.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val unitProvider: UnitProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedDataWeather.observeForever(Observer { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        })
    }

    override suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    // update weather entry in DB
    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) { currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry) }
    }

    private suspend fun initWeatherData() {
        if (isFetchCurrentWeatherNeeded(ZonedDateTime.now().minusHours(1)))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather() {
        val units = when (unitProvider.unitProvider()) {
            UnitSystem.METRIC -> METRIC_UNITS
            UnitSystem.IMPERIAL -> IMPERIAL_UNITS
            UnitSystem.SCIENTIFIC -> SCIENCE_UNITS
        }
        weatherNetworkDataSource.fetchCurrentWeather(DEFAULT_LOCATION, units)
    }

    private fun isFetchCurrentWeatherNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}