package com.kratosgado.statusaver.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
  private val notificationManager = NotificationManagerCompat.from(context)

  private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
  }
  private val dataStore = context.dataStore

  companion object Keys {
    val SAVE_LOCATION = stringPreferencesKey("save_location")
    val STATUS_LOCATION = stringPreferencesKey("status_location")
    val AUTO_SAVE = booleanPreferencesKey("auto_save")
    val NOTIFICATIONS = booleanPreferencesKey("notifications")
    val VIBRATION = booleanPreferencesKey("vibration")
    val THEME = booleanPreferencesKey("dark_theme")

    private val DEFAULT_SAVE_LOCATION = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      "StatusSaver"
    ).apply { if (!exists()) mkdir() }.toURI().toString()
  }

  suspend fun isInitialized(): Boolean {
    return dataStore.data.first().contains(STATUS_LOCATION)
  }

  // Save Location (URI)
  suspend fun setUri(uri: Uri, loc: Preferences.Key<String>) {
    when (loc) {
      SAVE_LOCATION -> {
        // Persist permission for save location
        context.contentResolver.takePersistableUriPermission(
          uri,
          android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
              android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
      }

      STATUS_LOCATION -> {
        // Persist permission for status location
        context.contentResolver.takePersistableUriPermission(
          uri,
          android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
      }
    }
    dataStore.edit { preferences ->
      preferences[loc] = uri.toString()
    }
  }

  val saveLocation: Flow<Uri?> = dataStore.data
    .map { preferences ->
      preferences[SAVE_LOCATION]?.let { Uri.parse(it) } ?: Uri.parse(DEFAULT_SAVE_LOCATION)
    }

  val statusLocation: Flow<Uri?> = dataStore.data.map { pref ->
    pref[STATUS_LOCATION]?.let { Uri.parse(it) }
  }

  // Auto Save
  suspend fun setAutoSave(enabled: Boolean) {
    dataStore.edit { preferences ->
      preferences[AUTO_SAVE] = enabled
    }
  }

  val autoSave: Flow<Boolean> = dataStore.data
    .map { preferences ->
      preferences[AUTO_SAVE] ?: false
    }

  // Notifications
  suspend fun setNotificationsEnabled(enabled: Boolean) {
    if (enabled) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        notificationManager.areNotificationsEnabled()
      }
    }
    dataStore.edit { preferences ->
      preferences[NOTIFICATIONS] = enabled
    }
  }

  val notificationsEnabled: Flow<Boolean> = dataStore.data
    .map { preferences ->
      preferences[NOTIFICATIONS] ?: true
    }

  // Vibration
  suspend fun setVibrationEnabled(enabled: Boolean) {
    if (enabled && vibrator.hasVibrator()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
      }
    }
    dataStore.edit { preferences ->
      preferences[VIBRATION] = enabled
    }
  }

  val vibrationEnabled: Flow<Boolean> = dataStore.data
    .map { preferences ->
      preferences[VIBRATION] ?: false
    }

  // Theme
  suspend fun setDarkTheme(enabled: Boolean) {
    dataStore.edit { preferences ->
      preferences[THEME] = enabled
    }
  }

  val darkTheme: Flow<Boolean> = dataStore.data
    .map { preferences ->
      preferences[THEME] ?: false
    }

  // Clear all preferences
  suspend fun clearCache() {
    val currentTheme = getValue(THEME, false)
    dataStore.edit { preferences ->
      preferences.clear()
      preferences[THEME] = currentTheme
    }
    // Clear saved files
    saveLocation.first()?.path?.let { path ->
      File(path).listFiles()?.forEach { it.delete() }
    }
  }

  // Generic getter (for Java compatibility)
  suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): T {
    return dataStore.data.map { preferences ->
      preferences[key] ?: defaultValue
    }.first()
  }
}