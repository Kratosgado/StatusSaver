package com.kratosgado.statussaver.utils

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener

class AppPreferences(context: Context) {
  private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

  fun <T> getValue(key: String, defaultValue: T): T {
    return when (defaultValue) {
      is String -> sharedPreferences.getString(key, defaultValue) as T
      is Int -> sharedPreferences.getInt(key, defaultValue) as T
      is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
      else -> throw IllegalArgumentException("Unsupported type")
    }
  }


  fun <T> putValue(key: String, value: T) {
    val editor = sharedPreferences.edit()
    when (value) {
      is String -> editor.putString(key, value)
      is Int -> editor.putInt(key, value)
      is Boolean -> editor.putBoolean(key, value)
      else -> throw IllegalArgumentException("Unsupported type")
    }
    editor.apply()
  }

  fun registerListener(listener: OnSharedPreferenceChangeListener) {
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
  }

  companion object {
    private var instance: AppPreferences? = null
    fun getInstance(context: Context): AppPreferences {
      if (instance == null) instance = AppPreferences(context)
      return instance!!
    }
  }

}