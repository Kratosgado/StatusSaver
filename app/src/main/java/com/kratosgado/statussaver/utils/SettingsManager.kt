package com.kratosgado.statussaver.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.widget.Toast
import androidx.work.WorkManager

class SettingsManager(private val context: Context) {
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

  fun saveLocation(uri: Uri) {
    context.contentResolver.takePersistableUriPermission(
      uri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )
    this.putValue(SAVE_LOCATION, uri.toString())
  }

  fun setAutoSave(value: Boolean) {
    putValue(AUTO_SAVE, value)
    if (value) scheduleAutoSave(context)
    else WorkManager.getInstance(context).cancelUniqueWork(AUTO_SAVE_WORKER)

  }

  fun clearCache() {
    context.cacheDir.deleteRecursively()
    Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
  }

  companion object {
    const val SAVE_LOCATION = "save_location"
    const val AUTO_SAVE = "auto_save"
    const val AUTO_SAVE_WORKER = "AutoSaveWork"
    const val THEME = "app_theme"
    const val NOTIFICATIONS = "notifications"
    const val VIBRATION = "vibration"
    const val STATUS_DIR = "status_dir"
    private var instance: SettingsManager? = null
    fun getInstance(context: Context): SettingsManager {
      if (instance == null) instance = SettingsManager(context)
      return instance!!
    }
  }

}