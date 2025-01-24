package com.kratosgado.statussaver.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kratosgado.statussaver.R
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.utils.SettingsManager


@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  val prefs = SettingsManager.getInstance(LocalContext.current)
  var downloadPath by remember { mutableStateOf(prefs.getValue(SettingsManager.SAVE_LOCATION, "")) }
  var autoDownload by remember { mutableStateOf(prefs.getValue(SettingsManager.AUTO_SAVE, false)) }
  var notificationsEnabled by remember {
    mutableStateOf(
      prefs.getValue(
        SettingsManager.NOTIFICATIONS,
        false
      )
    )
  }
  var vibrateOnNotification by remember {
    mutableStateOf(
      prefs.getValue(
        SettingsManager.VIBRATION,
        false
      )
    )
  }
  var darkMode by remember { mutableStateOf(prefs.getValue(SettingsManager.THEME, false)) }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri ->
    uri?.let {
      prefs.saveLocation(it)
    }
  }

  LazyColumn(modifier) {
    item {
      Text(
        text = stringResource(id = R.string.download_settings),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = R.string.download_path,
        value = downloadPath,
        onClick = { launcher.launch(null) }
      )
    }

    item {
      SettingItem(
        title = R.string.auto_download,
        value = autoDownload,
        onClick = {
          prefs.setAutoSave(it!!)
          autoDownload = it
        }
      )
    }
    item {
      Text(
        text = stringResource(id = R.string.notification),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = R.string.notification,
        value = notificationsEnabled,
        onClick = {
          prefs.putValue("notification", it)
          notificationsEnabled = it!!
        }
      )
    }
    item {
      SettingItem(
        title = R.string.notification_vibrate,
        value = vibrateOnNotification,
        onClick = {
          prefs.putValue("vibrateNotification", it)
          vibrateOnNotification = it!!
        }
      )
    }
    item {
      Text(
        text = stringResource(R.string.other_settings),
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = R.string.dark_mode,
        value = darkMode,
        onClick = {
          prefs.putValue("darkMode", it)
          darkMode = it!!
        }
      )
    }
    item {
      SettingItem(
        title = R.string.clear_cache,
        value = "clear",
        onClick = {prefs.clearCache()}
      )
    }
    item {
      SettingItem(
        title = R.string.about,
        value = "v1.0",
        onClick = { /* Handle about screen navigation */ }
      )
    }
  }
}

@Composable
fun <T> SettingItem(@StringRes title: Int, value: T, onClick: (T?) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(id = title),
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.weight(1f)
    )
    if (value is String) Text(
      modifier = Modifier.clickable { onClick(null) },
      text = value,
      style = MaterialTheme.typography.bodyLarge,
      color = Color.Gray
    ) else Switch(checked = value as Boolean, onCheckedChange = { onClick(it as T) })
  }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
  AppTheme(darkTheme = true) {
    SettingsScreen()
  }
}