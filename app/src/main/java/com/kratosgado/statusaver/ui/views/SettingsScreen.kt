package com.kratosgado.statusaver.ui.views

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kratosgado.statusaver.R
import com.kratosgado.statusaver.ui.components.PermissionDialog
import com.kratosgado.statusaver.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier,
  viewModel: SettingsViewModel = hiltViewModel()
) {
  val settingsState by viewModel.settingsState.collectAsState()
  val permissionState by viewModel.permissionState.collectAsState()

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    viewModel.onPermissionResult(
      Manifest.permission.READ_EXTERNAL_STORAGE,
      isGranted
    )
  }

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    viewModel.onPermissionResult(
      Manifest.permission.POST_NOTIFICATIONS,
      isGranted
    )
  }

  val manageStorageLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) {
    viewModel.onManageStoragePermissionResult()
  }
  val directoryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri: Uri? ->
    uri?.let { viewModel.setSaveLocation(it.toString()) }
  }
  LazyColumn(modifier) {
    item { SettingsHeader(title = stringResource(R.string.storage_settings)) }

    item {
      SettingsItem(
        title = stringResource(R.string.save_location),
        subtitle = settingsState.saveLocation.ifEmpty {
          stringResource(R.string.not_select)
        },
        onClick = { directoryLauncher.launch(null) }
      )
    }

    item {
      SwitchSetting(
        title = stringResource(R.string.auto_save),
        checked = settingsState.autoSave,
        onCheckedChange = viewModel::toggleAutoSave
      )
    }

    item { SettingsHeader(title = stringResource(R.string.notification_settings)) }

    item {
      SwitchSetting(
        title = stringResource(R.string.enable_notifications),
        checked = settingsState.notificationsEnabled,
        onCheckedChange = viewModel::toggleNotifications
      )
    }

    item {
      SwitchSetting(
        title = stringResource(R.string.enable_vibration),
        checked = settingsState.vibrationEnabled,
        onCheckedChange = viewModel::toggleVibration
      )
    }

    item { SettingsHeader(title = stringResource(R.string.appearance_settings)) }

    item {
      SwitchSetting(
        title = stringResource(R.string.dark_theme),
        checked = settingsState.darkTheme,
        onCheckedChange = viewModel::toggleDarkTheme
      )
    }

    item { SettingsHeader(title = stringResource(R.string.other)) }

    item {
      ActionSetting(
        title = stringResource(R.string.clear_cache),
        onClick = viewModel::clearCache
      )
    }

    item {
      SettingsItem(
        title = stringResource(R.string.app_version),
        subtitle = settingsState.appVersion
      )
    }
  }
  PermissionDialog(
    showDialog = permissionState.shouldShowPermissionDialog,
    hasStoragePermission = permissionState.hasStoragePermission,
    hasNotificationPermission = permissionState.hasNotificationPermission,
    hasManageStoragePermission = permissionState.hasManageStoragePermission,
    onDismiss = viewModel::dismissPermissionDialog,
    onRequestStoragePermission = {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        manageStorageLauncher.launch(viewModel.getManageStorageIntent())
      } else {
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
      }
    },
    onRequestNotificationPermission = {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    },
    onRequestManageStorage = {
      manageStorageLauncher.launch(viewModel.getManageStorageIntent())
    }
  )
}

@Composable
private fun SettingsHeader(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  )
}

@Composable
private fun SettingsItem(
  title: String,
  subtitle: String,
  onClick: (() -> Unit)? = null
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun SwitchSetting(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .toggleable(
        value = checked,
        onValueChange = onCheckedChange
      ),
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
      )
      Switch(
        checked = checked,
        onCheckedChange = null
      )
    }
  }
}

@Composable
private fun ActionSetting(
  title: String,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    tonalElevation = 2.dp
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(16.dp)
    )
  }
}