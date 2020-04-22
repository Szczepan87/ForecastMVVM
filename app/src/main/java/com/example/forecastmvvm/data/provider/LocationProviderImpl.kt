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

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (isUsingDeviceLocation().not()) return false

        val deviceLocation = getLastDeviceLocation().await() ?: return false

        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > LOCATION_COMPARISON_THRESHOLD &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > LOCATION_COMPARISON_THRESHOLD
    }

    private fun isUsingDeviceLocation() = preferences.getBoolean(USE_DEVICE_LOCATION, true)

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