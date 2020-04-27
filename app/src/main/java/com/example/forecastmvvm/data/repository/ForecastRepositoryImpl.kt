package com.example.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.WeatherLocationDao
import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.example.forecastmvvm.data.db.entity.WeatherLocation
import com.example.forecastmvvm.data.network.WeatherNetworkDataSource
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.forecastmvvm.data.provider.LocationProvider
import com.example.forecastmvvm.data.provider.UnitProvider
import com.example.forecastmvvm.internal.IMPERIAL_UNITS
import com.example.forecastmvvm.internal.METRIC_UNITS
import com.example.forecastmvvm.internal.SCIENCE_UNITS
import com.example.forecastmvvm.internal.UnitSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

/**
 * Repository for connecting remote and local data.
 * Injecting: DAO of Weather and Location, Providers of Unit of measurement and current Location.
 * Also injecting remote weather service.
 */
class ForecastRepositoryImpl(
        private val currentWeatherDao: CurrentWeatherDao,
        private val weatherNetworkDataSource: WeatherNetworkDataSource,
        private val unitProvider: UnitProvider,
        private val weatherLocationDao: WeatherLocationDao,
        private val locationProvider: LocationProvider
) : ForecastRepository {

    /**
     * Observing changes on LiveData containing WeatherResponse. Changes occur when
     * WeatherNetworkDataSource.fetchCurrentWeather() is called.
     */
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

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    /**
     * Updating weather in DB
     * Updating current weather location in DB
     */
    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.weatherLocation)
        }
    }

    /**
     * If there are no last weather location is null
     * or location is different than stored in DB
     * or weather data is older than 30 minutes
     * then it fetches fresh weather data
     */
    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocation().value

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            return
        }

        if (isFetchCurrentWeatherNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()
    }

    /**
     * Calls WeatherNetworkDataSource.fetchCurrentWeather() and passes units of measurement
     * from settings and current location to function parameters.
     */
    private suspend fun fetchCurrentWeather() {
        val units = when (unitProvider.unitProvider()) {
            UnitSystem.METRIC -> METRIC_UNITS
            UnitSystem.IMPERIAL -> IMPERIAL_UNITS
            UnitSystem.SCIENTIFIC -> SCIENCE_UNITS
        }
        weatherNetworkDataSource.fetchCurrentWeather(
                locationProvider.getPreferredLocationString(),
                units
        )
    }

    /**
     * Checks for current time. If provided time is more than 30 minutes in the past
     * then returns true. It means that weather data needs to be refreshed.
     */
    private fun isFetchCurrentWeatherNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}