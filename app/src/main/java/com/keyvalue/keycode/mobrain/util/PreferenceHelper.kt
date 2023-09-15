package com.keyvalue.keycode.mobrain.util

import android.content.Context

object PreferenceHelper {
    private const val PREF_FILE_MOBRAIN = "MOBRAIN_DATA"

    const val DEVICE_ID = "DEVICE_ID"
    const val SECRET = "SECRET"


    internal fun setSharedPreferenceString(context: Context, key: String, value: String) {
        val settings =
            context.getSharedPreferences(PREF_FILE_MOBRAIN, 0)
        val editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    internal fun getSharedPreferenceString(
        context: Context,
        key: String,
    ): String {
        val settings =
            context.getSharedPreferences(PREF_FILE_MOBRAIN, 0)

        return settings.getString(key, "") ?: ""
    }

    internal fun clearAllDataPref(context: Context) {
        val settings = context.getSharedPreferences(PREF_FILE_MOBRAIN, 0)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
    }

    internal fun clearSpecificDataPref(context: Context, key: String) {
        val settings = context.getSharedPreferences(PREF_FILE_MOBRAIN, 0)
        val editor = settings.edit()
        editor.remove(key)
        editor.apply()
    }


}