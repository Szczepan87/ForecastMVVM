package com.example.forecastmvvm.data.provider

import android.content.Context
import com.example.forecastmvvm.internal.UNIT_SYSTEM
import com.example.forecastmvvm.internal.UnitSystem

/**
 * Class used for providing metric system currently used in the app.
 */
class UnitProviderImpl(context: Context) : PreferenceProvider(context), UnitProvider {

    override fun unitProvider(): UnitSystem {
        val selectedName = preferences.getString(UNIT_SYSTEM, UnitSystem.METRIC.name)
        return UnitSystem.valueOf(selectedName!!)
    }
}