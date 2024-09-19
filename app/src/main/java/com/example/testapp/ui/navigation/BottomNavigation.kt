package com.example.testapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelect: (Int) -> Unit) {
  NavigationBar {
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Images") },
      label = { Text(text = "Images") },
      selected = selectedItem == 0,
      onClick = { onItemSelect(0) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Share, contentDescription = "Videos") },
      label = { Text(text = "Videos") },
      selected = selectedItem == 1,
      onClick = { onItemSelect(1) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Send, contentDescription = "Saved") },
      label = { Text(text = "Saved") },
      selected = selectedItem == 2,
      onClick = { onItemSelect(2) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Share, contentDescription = "Settings") },
      label = { Text(text = "Settings") },
      selected = selectedItem == 3,
      onClick = { onItemSelect(3) }
    )
  }
}
