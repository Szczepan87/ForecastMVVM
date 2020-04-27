package com.example.forecastmvvm.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.forecastmvvm.data.db.entity.WeatherLocation
import com.example.forecastmvvm.internal.*
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred
import kotlin.math.abs

/**
 * Custom location provider for dependant classes which need info about current location.
 * Uses injected FusedLocationProviderClient from google.gms library.
 */
class LocationProviderImpl(
        context: Context,
        private val fusedLocationProviderClient: FusedLocationProviderClient
) : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    /**
     * Returns currently used location.
     * If location provided by FusedLocationProviderClient fails then it returns location currently
     * set manually in settings.
     */
    override suspend fun getPreferredLocationString(): String {
        return if (isUsingDeviceLocation()) {
            try {
                val deviceLocation =
                        getLastDeviceLocation().await() ?: return getCustomLocationName()
                "${deviceLocation.latitude},${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                getCustomLocationName()
            }
        } else getCustomLocationName()
    }

    /**
     * Accepts lastWeatherLocation and compares it to the location stored in database.
     * If user is providing custom location in setting then function returns false.
     */
    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (isUsingDeviceLocation().not()) return false

        val deviceLocation = getLastDeviceLocation().await() ?: return false

        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > LOCATION_COMPARISON_THRESHOLD &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > LOCATION_COMPARISON_THRESHOLD
    }

    private fun isUsingDeviceLocation() = preferences.getBoolean(USE_DEVICE_LOCATION, true)

    /**
     * Getting Deferred form FusedLocationProviderClient. Allows for using await() later on.
     */
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation) =
            getCustomLocationName() != lastWeatherLocation.name

    private fun getCustomLocationName(): String =
            preferences.getString(CUSTOM_LOCATION, DEFAULT_LOCATION) ?: DEFAULT_LOCATION
}