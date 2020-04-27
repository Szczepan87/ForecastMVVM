package com.example.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.example.forecastmvvm.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
        private val apixuWeatherApiService: ApixuWeatherApiService
) : WeatherNetworkDataSource {
    /**
     * Providing public LiveData with CurrentWeatherResponse.
     */
    private val _downloadedDataWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedDataWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedDataWeather

    /**
     * Getting Deffered about current weather from remote service.
     * Catching custom NoConnectivityException.
     */
    override suspend fun fetchCurrentWeather(location: String, units: String) {
        try {
            val fetchedCurrentWeather =
                    apixuWeatherApiService.getCurrentWeather(location, units)
                            .await()
            _downloadedDataWeather.postValue(fetchedCurrentWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet!", e)
        }
    }
}