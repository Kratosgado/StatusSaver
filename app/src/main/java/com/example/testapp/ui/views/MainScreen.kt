package com.example.testapp.ui.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.views.components.AppBar
import com.example.testapp.ui.views.components.BottomNavigationBar


@Composable
fun MainScreen(directory: String, savedDir: String = "", modifier: Modifier = Modifier) {
  var selectedItem by remember { mutableIntStateOf(0) }

  AppTheme {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding(),
      topBar = { AppBar() },
      bottomBar = { BottomNavigationBar(selectedItem, onItemSelect = { selectedItem = it }) },
    ) {
      when (selectedItem) {
        1 -> VideosScreen(directory = directory, modifier = Modifier.padding(it))
        2 -> SavedScreen(directory = savedDir, modifier = Modifier.padding(it))
        3 -> SettingsScreen(modifier = Modifier.padding(it))
        else -> ImagesScreen(
          modifier = Modifier.padding(it),
          directory = directory
        )
      }
    }
  }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
  val dir = "/home/kratosgado/Pictures/Camera/"
  AppTheme {
    MainScreen(dir, dir)
  }
}
