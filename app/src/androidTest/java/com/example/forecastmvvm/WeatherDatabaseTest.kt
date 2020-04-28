package com.example.forecastmvvm

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.forecastmvvm.data.db.CurrentWeatherDao
import com.example.forecastmvvm.data.db.ForecastDatabase
import com.example.forecastmvvm.data.db.entity.CurrentWeatherEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest {
    private lateinit var weatherDao: CurrentWeatherDao
    private lateinit var database: ForecastDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ForecastDatabase::class.java).build()
        weatherDao = database.currentWeatherDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun shouldReadAndWriteToDatabase() {
        val weatherEntry = CurrentWeatherEntry(0.0,
                0.0,
                0.0,
                "no",
                "01-11-1987 11:41",
                0.0,
                0.0,
                0.0,
                0.0,
                0,
                listOf("abc"),
                listOf("def"),
                0.0,
                "N",
                0.0)
        runBlocking { weatherDao.upsert(weatherEntry) }
        GlobalScope.launch(Dispatchers.IO) { weatherDao.getCurrentWeather().value.let { assertEquals(weatherEntry, it) } }
    }
}