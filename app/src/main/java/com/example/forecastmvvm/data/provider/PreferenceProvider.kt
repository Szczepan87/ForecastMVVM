package com.example.forecastmvvm.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Abstract class providing SharedPreferences.
 */
abstract class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext
    protected val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)
}