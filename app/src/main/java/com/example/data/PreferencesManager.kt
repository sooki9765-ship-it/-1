package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("class_reading_prefs", Context.MODE_PRIVATE)

    var gasWebAppUrl: String
        get() = prefs.getString("gas_web_app_url", "") ?: ""
        set(value) = prefs.edit().putString("gas_web_app_url", value).apply()

    var teacherPassword: String
        get() = prefs.getString("teacher_password", "1234") ?: "1234"
        set(value) = prefs.edit().putString("teacher_password", value).apply()

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean("is_first_launch", true)
        set(value) = prefs.edit().putBoolean("is_first_launch", value).apply()
}
