package com.example.testapp.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testapp.ui.views.MainScreen

enum class Screen() {
  Main,
  Settings,
  StatusView,
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
  NavHost(
    navController = navController, startDestination = Screen.Main.name,
    modifier = modifier.fillMaxSize()
  ) {
    composable(Screen.Main.name) { MainScreen(directory = "") }
    composable(Screen.StatusView.name) {
//      ImagePager(modifier = modifier, files = files, startIndex = viewImage.second) {
//        viewImage = false to 0
//      }
    }
  }
}