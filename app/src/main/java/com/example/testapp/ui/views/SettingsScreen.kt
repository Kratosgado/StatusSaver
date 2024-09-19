package com.example.testapp.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.theme.AppTheme


@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  var downloadPath by remember { mutableStateOf("/storage/emulated/0/Download") }
  var fileFormat by remember { mutableStateOf("jpg") }
  var downloadQuality by remember {
    mutableStateOf(
      "High"
    )
  }
  var autoDownload by remember {
    mutableStateOf(
      false
    )
  }
  var notificationsEnabled by remember {
    mutableStateOf(
      true
    )
  }
  var notificationSound by remember {
    mutableStateOf(
      "Default"
    )
  }
  var vibrateOnNotification by remember {
    mutableStateOf(
      false
    )
  }
  var darkMode by remember { mutableStateOf(false) }
  var language by remember { mutableStateOf("English") }

  LazyColumn(modifier = Modifier.fillMaxSize()) {
    item {
      Text(
        text = "Download Settings",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = "Download Path",
        value = downloadPath,
        onClick = { /* Handle download path selection */ }
      )
    }
    item {
      SettingItem(
        title = "File Format",
        value = fileFormat,
        onClick = { /* Handle file format selection */ }
      )
    }
    item {
      SettingItem(
        title = "Download Quality",
        value = downloadQuality,
        onClick = { /* Handle download quality selection */ }
      )
    }
    item {
      SettingItem(
        title = "Auto-download Media",
        value = if (autoDownload) "On" else "Off",
        onClick = { autoDownload = !autoDownload }
      )
    }
    item {
      Text(
        text = "Notifications",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = "Notifications",
        value = if (notificationsEnabled) "On" else "Off",
        onClick = { notificationsEnabled = !notificationsEnabled }
      )
    }
    item {
      SettingItem(
        title = "Notification Sound",
        value = notificationSound,
        onClick = { /* Handle notification sound selection */ }
      )
    }
    item {
      SettingItem(
        title = "Vibrate on Notification",
        value = if (vibrateOnNotification) "On" else "Off",
        onClick = { vibrateOnNotification = !vibrateOnNotification }
      )
    }
    item {
      Text(
        text = "Other Settings",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(16.dp)
      )
    }
    item {
      SettingItem(
        title = "Dark Mode",
        value = if (darkMode) "On" else "Off",
        onClick = { darkMode = !darkMode }
      )
    }
    item {
      SettingItem(
        title = "Language",
        value = language,
        onClick = { /* Handle language selection */ }
      )
    }
    item {
      SettingItem(
        title = "Clear Cache",
        value = "",
        onClick = { /* Handle cache clearing */ }
      )
    }
    item {
      SettingItem(
        title = "About",
        value = "",
        onClick = { /* Handle about screen navigation */ }
      )
    }
  }
}

@Composable
fun SettingItem(title: String, value: String, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.weight(1f)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyLarge,
      color = Color.Gray
    )
  }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
  AppTheme {
    SettingsScreen()
  }
}