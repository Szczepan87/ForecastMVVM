package com.example.forecastmvvm.data.repository

import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.WeatherLocationDao
import com.example.forecastmvvm.data.network.WeatherNetworkDataSourceImpl
import com.example.forecastmvvm.data.provider.LocationProviderImpl
import com.example.forecastmvvm.data.provider.UnitProviderImpl
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ForecastRepositoryImplTest {

    private lateinit var forecastRepositoryImpl: ForecastRepositoryImpl
    private lateinit var currentWeatherDao: CurrentWeatherDao
    private lateinit var locationProviderImpl: LocationProviderImpl
    private lateinit var unitProviderImpl: UnitProviderImpl
    private lateinit var weatherLocationDao: WeatherLocationDao
    private lateinit var weatherNetworkDataSourceImpl: WeatherNetworkDataSourceImpl


    @Before
    fun setUp() {
        currentWeatherDao = mock(CurrentWeatherDao::class.java)
        locationProviderImpl = mock(LocationProviderImpl::class.java)
        unitProviderImpl = mock(UnitProviderImpl::class.java)
        weatherLocationDao = mock(WeatherLocationDao::class.java)
        weatherNetworkDataSourceImpl = mock(WeatherNetworkDataSourceImpl::class.java)
        forecastRepositoryImpl = ForecastRepositoryImpl(
                currentWeatherDao,
                weatherNetworkDataSourceImpl,
                unitProviderImpl,
                weatherLocationDao,
                locationProviderImpl)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun repositoryTest() {
        runBlocking { verify(forecastRepositoryImpl).getCurrentWeather() }
        runBlocking { verify(forecastRepositoryImpl).getWeatherLocation() }
    }
}