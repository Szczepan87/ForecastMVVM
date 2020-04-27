package com.example.forecastmvvm.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.forecastmvvm.R
import com.example.forecastmvvm.internal.MY_PERMISSION_ACCESS_COARSE_LOCATION
import com.example.forecastmvvm.ui.settings.SettingsFragmentDirections
import com.example.forecastmvvm.ui.weather.current.CurrentWeatherFragmentDirections
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val fusedLocationProviderClient: FusedLocationProviderClient by instance<FusedLocationProviderClient>()

    private val locationCallback = object : LocationCallback() {}
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(toolbar)
        requestLocationPermission()

        if (hasLocationPermission()) {
            bindLocationManager()
        } else requestLocationPermission()
    }

    /**
     * Binds custom Location Manager that is connected to Activity Lifecycle.
     */
    private fun bindLocationManager() {
        LifecycleBoundLocationManager(this, fusedLocationProviderClient, locationCallback)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSION_ACCESS_COARSE_LOCATION
        )
    }

    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSION_ACCESS_COARSE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
                bindLocationManager()
            else
                Toast.makeText(
                        this,
                        "Please, set location manually in settings!",
                        Toast.LENGTH_LONG
                )
                        .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToCurrentWeatherFragment()
                navController.navigate(action)
                return true
            }
            R.id.settingsFragment -> {
                val action = CurrentWeatherFragmentDirections.actionCurrentWeatherFragmentToSettingsFragment()
                navController.navigate(action)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
