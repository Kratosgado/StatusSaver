package com.example.testapp.ui.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testapp.ui.navigation.BottomNavigationBar
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.views.components.AppBar


@Composable
fun MainScreen(directory: String, modifier: Modifier = Modifier) {
  var selectedItem by remember { mutableStateOf(0) }

  val items = listOf("Images", "Videos", "Saved", "Settings")
  AppTheme {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { AppBar() },
      bottomBar = { BottomNavigationBar(selectedItem, onItemSelect = { selectedItem = it }) },
//      floatingActionButton = { Text(text = "Hello") }
    ) {
      when (selectedItem) {
        0 -> ImagesScreen(
          modifier = Modifier.padding(it),
          directory = directory
        )

        1 -> VideosScreen(modifier = Modifier.padding(it))
        2 -> SavedScreen(modifier = Modifier.padding(it))
        3 -> SettingsScreen(modifier = Modifier.padding(it))

      }
    }
  }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
  AppTheme {
    MainScreen(directory = "/home/kratosgado/Pictures/Camera/")
  }
}
