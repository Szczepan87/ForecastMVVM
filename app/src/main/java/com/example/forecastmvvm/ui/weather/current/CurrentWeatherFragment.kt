package com.example.forecastmvvm.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.forecastmvvm.R
import com.example.forecastmvvm.internal.UnitSystem
import com.example.forecastmvvm.internal.glide.GlideApp
import com.example.forecastmvvm.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()

    private val viewModelFactory: CurrentWeatherViewModelFactory by instance<CurrentWeatherViewModelFactory>()

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CurrentWeatherViewModel::class.java)
        bindUI()
    }

    /**
     * Launches from CoroutineScope provided by ScopedFragment() and awaits for weather
     * and weather location data to be initialized. Observes values and changes UI accordingly.
     */
    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        currentWeather.observe(
                viewLifecycleOwner,
                Observer {
                    if (it == null) return@Observer
                    group_loading.visibility = View.GONE
                    updateDateToToday()
                    updateTemperatures(it.temperature, it.feelslike)
                    updateCondition(it.weatherDescriptions.first())
                    updatePrecipitation(it.precip)
                    updateWind(it.windDir, it.windSpeed)
                    updateVisibility(it.visibility)
                    updateDarkMode(it.isDay)

                    GlideApp.with(this@CurrentWeatherFragment)
                            .load(it.weatherIcons.first())
                            .into(imageView_condition_icon)
                })
    }

    private fun chooseLocalisedUnitAbbreviation(
            metric: String,
            imperial: String,
            scientific: String
    ): String {
        return when (viewModel.unitSystem) {
            UnitSystem.METRIC -> metric
            UnitSystem.IMPERIAL -> imperial
            UnitSystem.SCIENTIFIC -> scientific
        }
    }

    /**
     * A series of function that updates UI. DataBinding is not used in this app.
     */

    private fun updateLocation(location: String) {
        (activity as AppCompatActivity).supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as AppCompatActivity).supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        val unitAbbreviation = chooseLocalisedUnitAbbreviation("C", "F", "K")
        textView_temperature.text = "$temperature $unitAbbreviation"
        textView_feels_like_temperature.text = "Feels like: $feelsLike $unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation = chooseLocalisedUnitAbbreviation("mm", "in", "mm")
        textView_precipitation.text = "Precipitation: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalisedUnitAbbreviation("km/h", "mi/h", "m/s")
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalisedUnitAbbreviation("km", "mi", "km")
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }

    private fun updateDarkMode(isDay: String) {
        when (isDay) {
            "no" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "yes" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
