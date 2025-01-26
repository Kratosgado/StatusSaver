package com.kratosgado.statussaver.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kratosgado.statussaver.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val settingsManager: SettingsManager
) : ViewModel() {

  private val _settingsState = MutableStateFlow(SettingsState())
  val settingsState: StateFlow<SettingsState> = _settingsState

  init {
    combine(
      settingsManager.saveLocation,
      settingsManager.autoSave,
      settingsManager.notificationsEnabled,
      settingsManager.vibrationEnabled,
      settingsManager.darkTheme
    ) { saveLocation, autoSave, notifications, vibration, darkTheme ->
      SettingsState(
        saveLocation = saveLocation.toString(),
        autoSave = autoSave,
        notificationsEnabled = notifications,
        vibrationEnabled = vibration,
        darkTheme = darkTheme
      )
    }.onEach { _settingsState.value = it }
      .launchIn(viewModelScope)
  }

  fun setSaveLocation(uri: String) {
    viewModelScope.launch {
      settingsManager.saveLocation(android.net.Uri.parse(uri))
    }
  }

  fun toggleAutoSave(enabled: Boolean) {
    viewModelScope.launch {
      settingsManager.setAutoSave(enabled)
    }
  }

  fun toggleNotifications(enabled: Boolean) {
    viewModelScope.launch {
      settingsManager.setNotificationsEnabled(enabled)
    }
  }

  fun toggleVibration(enabled: Boolean) {
    viewModelScope.launch {
      settingsManager.setVibrationEnabled(enabled)
    }
  }

  fun toggleDarkTheme(enabled: Boolean) {
    viewModelScope.launch {
      settingsManager.setDarkTheme(enabled)
    }
  }

  fun clearCache() {
    viewModelScope.launch {
      settingsManager.clearCache()
    }
  }
}

data class SettingsState(
  val saveLocation: String = "",
  val autoSave: Boolean = false,
  val notificationsEnabled: Boolean = true,
  val vibrationEnabled: Boolean = false,
  val darkTheme: Boolean = false,
  val appVersion: String = "1.0.0"
)