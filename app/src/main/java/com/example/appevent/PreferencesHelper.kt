package com.example.appevent

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    fun saveNotificationSetting(isEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, isEnabled)
        editor.apply()
    }

    fun getNotificationSetting(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, false)
    }
}
