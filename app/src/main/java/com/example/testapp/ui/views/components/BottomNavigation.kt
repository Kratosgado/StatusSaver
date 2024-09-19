package com.example.testapp.ui.views.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testapp.ui.theme.AppTheme


@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
  NavigationBar (modifier,
//    containerColor = MaterialTheme.colorScheme.primaryContainer
    ){
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

@Preview(showBackground = true)
@Composable
fun PreviewNavigationBar(){
  AppTheme {
    BottomNavigationBar(selectedItem = 1, onItemSelect = {})
  }
}