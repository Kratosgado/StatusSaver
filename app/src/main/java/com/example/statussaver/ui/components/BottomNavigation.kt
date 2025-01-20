package com.example.statussaver.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.statussaver.ui.Screens
import com.example.statussaver.ui.theme.AppTheme


@Composable
fun BottomNavigationBar(
  selectedScreen: Screens,
  onItemSelect: (Screens) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier,
//    containerColor = MaterialTheme.colorScheme.primaryContainer
  ) {
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Images") },
      label = { Text(text = "Images") },
      selected = selectedScreen == Screens.Images,
      onClick = { onItemSelect(Screens.Images) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Videos") },
      label = { Text(text = "Videos") },
      selected = selectedScreen == Screens.Videos,
      onClick = { onItemSelect(Screens.Videos) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Saved") },
      label = { Text(text = "Saved") },
      selected = selectedScreen == Screens.Saved,
      onClick = { onItemSelect(Screens.Saved) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
      label = { Text(text = "Settings") },
      selected = selectedScreen == Screens.Settings,
      onClick = { onItemSelect(Screens.Settings) }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavigationBar() {
  AppTheme {
    BottomNavigationBar(selectedScreen = Screens.Images, onItemSelect = {})
  }
}