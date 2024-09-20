package com.example.testapp.ui.views.components

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
import com.example.testapp.ui.theme.AppTheme


@Composable
fun BottomNavigationBar(
  selectedItem: Int,
  onItemSelect: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier,
//    containerColor = MaterialTheme.colorScheme.primaryContainer
  ) {
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Images") },
      label = { Text(text = "Images") },
      selected = selectedItem == 0,
      onClick = { onItemSelect(0) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Videos") },
      label = { Text(text = "Videos") },
      selected = selectedItem == 1,
      onClick = { onItemSelect(1) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Saved") },
      label = { Text(text = "Saved") },
      selected = selectedItem == 2,
      onClick = { onItemSelect(2) }
    )
    NavigationBarItem(
      icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
      label = { Text(text = "Settings") },
      selected = selectedItem == 3,
      onClick = { onItemSelect(3) }
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavigationBar() {
  AppTheme {
    BottomNavigationBar(selectedItem = 1, onItemSelect = {})
  }
}