package com.example.weatherhitapi

import android.content.Context
import android.preference.PreferenceManager

@Suppress("DEPRECATION")
class SharedPreferences(context: Context) {
    companion object {
        private const val SIGNATURE = "SIGNATURE"
    }

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

    var signature = preferenceManager.getString(SIGNATURE, "")
        set(value) = preferenceManager.edit().putString(SIGNATURE, value).apply()
}