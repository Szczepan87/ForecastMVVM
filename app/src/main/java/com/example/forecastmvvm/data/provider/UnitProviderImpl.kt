package com.example.forecastmvvm.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.forecastmvvm.internal.UnitSystem

class UnitProviderImpl(context: Context) : UnitProvider {

    private val appContext = context.applicationContext
    private val prefernces: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun unitProvider(): UnitSystem {
        val selectedName = prefernces.getString("UNIT_SYSTEM", UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }
}