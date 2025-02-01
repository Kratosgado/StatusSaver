package com.kratosgado.statussaver.data

import android.content.Context
import android.net.Uri
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
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext context: Context) {

  private val dataStore = context.dataStore

  companion object Keys {
    val SAVE_LOCATION = stringPreferencesKey("save_location")
    val STATUS_LOCATION = stringPreferencesKey("status_location")
    val AUTO_SAVE = booleanPreferencesKey("auto_save")
    val NOTIFICATIONS = booleanPreferencesKey("notifications")
    val VIBRATION = booleanPreferencesKey("vibration")
    val THEME = booleanPreferencesKey("dark_theme")
  }

  // Save Location (URI)
  suspend fun setUri(uri: Uri, loc: Preferences.Key<String>) {
    dataStore.edit { preferences ->
      preferences[loc] = uri.toString()
    }
  }

  val saveLocation: Flow<Uri?> = dataStore.data
    .map { preferences ->
      preferences[SAVE_LOCATION]?.let { Uri.parse(it) }
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
    dataStore.edit { preferences ->
      preferences.clear()
    }
  }

  // Generic getter (for Java compatibility)
  suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): T {
    return dataStore.data.map { preferences ->
      preferences[key] ?: defaultValue
    }.first()
  }
}