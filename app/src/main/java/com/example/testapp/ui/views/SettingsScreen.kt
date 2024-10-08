package com.example.testapp.ui.views

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.R
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.utils.AppPreferences


@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  val downloadPath = "/storage/emulated/0/StatusSaver"
  val fileFormat = ".jpg"
  val autoDownload = false
  val notificationsEnabled = true
  val vibrateOnNotification = false
  val darkMode = false

  val prefs = AppPreferences.getInstance(LocalContext.current)

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
        value = prefs.getValue("downloadPath", downloadPath),
        onClick = { /* todo: Handle download path selection */ }
      )
    }
    item {
      SettingItem(
        title = R.string.file_format,
        value = prefs.getValue("fileFormat", fileFormat),
        onClick = { /* todo:  Handle file format selection */ }
      )
    }
    item {
      SettingItem(
        title = R.string.auto_download,
        value = prefs.getValue("autoDownload", autoDownload),
        onClick = { prefs.putValue("autoDownload", it) }
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
        value = prefs.getValue("notification", notificationsEnabled),
        onClick = { prefs.putValue("notification", it) }
      )
    }
    item {
      SettingItem(
        title = R.string.notification_vibrate,
        value = prefs.getValue("vibrateNotification", vibrateOnNotification),
        onClick = { prefs.putValue("vibrateNotification", it) }
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
        value = prefs.getValue("darkMode", darkMode),
        onClick = { prefs.putValue("darkMode", it) }
      )
    }
    item {
      SettingItem(
        title = R.string.clear_cache,
        value = "",
        onClick = { /* todo:  Handle cache clearing */ }
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
  AppTheme {
    SettingsScreen()
  }
}