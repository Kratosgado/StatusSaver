package com.kratosgado.statussaver.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kratosgado.statussaver.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val settingsManager: SettingsManager,
  @ApplicationContext private val context: Context
) : ViewModel() {

  private val _settingsState = MutableStateFlow(SettingsState())
  val settingsState: StateFlow<SettingsState> = _settingsState

  private val _isLoading = MutableStateFlow(true)
  val isLoading = _isLoading.asStateFlow()

  private val _hasCheckedSettings = MutableStateFlow(false)
  val hasCheckedSettings = _hasCheckedSettings.asStateFlow()
  private val _permissionState = MutableStateFlow(PermissionState())
  val permissionState: StateFlow<PermissionState> = _permissionState

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
        darkTheme = darkTheme,
        appVersion = context.packageManager.getPackageInfo(
          context.packageName,
          0
        ).versionName.toString()
      )
    }.onEach {
      _settingsState.value = it
      _isLoading.value = false
    }.launchIn(viewModelScope)

    checkPermissions()
  }

  fun ready() {
    _isLoading.value = false
    _hasCheckedSettings.value = true
  }

  private fun checkPermissions() {
    viewModelScope.launch {
      _permissionState.value = PermissionState(
        hasStoragePermission = checkStoragePermission(),
        hasNotificationPermission = checkNotificationPermission(),
        hasManageStoragePermission = checkManageStoragePermission()
      )
    }
  }

  fun checkAndRequestPermissions() {
    checkPermissions()
    _permissionState.value = _permissionState.value.copy(
      shouldShowPermissionDialog = !hasRequiredPermissions()
    )
  }

  private fun hasRequiredPermissions(): Boolean {
    return _permissionState.value.run {
      hasStoragePermission &&
          (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) hasNotificationPermission else true) &&
          (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) hasManageStoragePermission else true)
    }
  }

  private fun checkStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Environment.isExternalStorageManager()
    } else {
      checkBasicStoragePermission()
    }
  }

  private fun checkBasicStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
  }

  private fun checkNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  private fun checkManageStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      Environment.isExternalStorageManager()
    } else {
      true
    }
  }

  fun getManageStorageIntent(): Intent {
    return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
      data = Uri.parse("package:${context.packageName}")
    }
  }

  fun getNotificationSettingsIntent(): Intent {
    return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
      putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
  }

  fun onPermissionResult(
    permission: String,
    isGranted: Boolean
  ) {
    when (permission) {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
        _permissionState.value = _permissionState.value.copy(
          hasStoragePermission = isGranted || checkStoragePermission()
        )
      }

      Manifest.permission.POST_NOTIFICATIONS -> {
        _permissionState.value = _permissionState.value.copy(
          hasNotificationPermission = isGranted
        )
      }
    }
    // Update dialog state
    _permissionState.value = _permissionState.value.copy(
      shouldShowPermissionDialog = !hasRequiredPermissions()
    )
  }

  fun onManageStoragePermissionResult() {
    _permissionState.value = _permissionState.value.copy(
      hasManageStoragePermission = checkManageStoragePermission()
    )
  }

  fun dismissPermissionDialog() {
    _permissionState.value = _permissionState.value.copy(
      shouldShowPermissionDialog = false
    )
  }

  // Existing settings methods...
  fun setSaveLocation(uri: String) {
    viewModelScope.launch {
      settingsManager.setUri(Uri.parse(uri), SettingsManager.SAVE_LOCATION)
    }
  }

  fun setStatusLocation(uri: Uri) {
    viewModelScope.launch {
      settingsManager.setUri(uri, SettingsManager.STATUS_LOCATION)
    }
  }

  fun toggleAutoSave(enabled: Boolean) {
    viewModelScope.launch {
      if (enabled) {
        checkAndRequestPermissions()
        if (hasRequiredPermissions()) {
          settingsManager.setAutoSave(true)
        }
      } else {
        settingsManager.setAutoSave(false)
      }
    }
  }

  fun toggleNotifications(enabled: Boolean) {
    viewModelScope.launch {
      if (enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (checkNotificationPermission()) {
            settingsManager.setNotificationsEnabled(true)
          } else {
            _permissionState.value = _permissionState.value.copy(
              shouldShowPermissionDialog = true
            )
          }
        } else {
          settingsManager.setNotificationsEnabled(true)
        }
      } else {
        settingsManager.setNotificationsEnabled(false)
      }
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

data class PermissionState(
  val hasStoragePermission: Boolean = false,
  val hasNotificationPermission: Boolean = false,
  val hasManageStoragePermission: Boolean = false,
  val shouldShowPermissionDialog: Boolean = false
)

data class SettingsState(
  val saveLocation: String = "",
  val autoSave: Boolean = false,
  val notificationsEnabled: Boolean = true,
  val vibrationEnabled: Boolean = false,
  val darkTheme: Boolean = false,
  val appVersion: String = "1.0.0"
)