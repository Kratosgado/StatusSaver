package com.example.testapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.components.AppBar
import com.example.testapp.ui.components.BottomNavigationBar
import com.example.testapp.ui.viewmodel.AppViewModel
import com.example.testapp.ui.views.ImagesScreen
import com.example.testapp.ui.views.SavedScreen
import com.example.testapp.ui.views.SettingsScreen
import com.example.testapp.ui.views.StatusPager
import com.example.testapp.ui.views.VideosScreen
import java.io.File



@Composable
fun MainScreen(
  appViewModel: AppViewModel = viewModel(),
  directory: String,
  savedDir: String = "",
) {
  val appState by appViewModel.uiState.collectAsState()
//  val currentScreen =
//    Screens.valueOf(backStackEntry?.destination?.route?.split('/')?.get(0) ?: Screens.Images.name)

  val whatsappStatusDir = File(directory)
  val savedFiles = File(savedDir)
  val images: MutableList<Pair<File, Boolean>> = mutableListOf()
  val videos: MutableList<Pair<File, Boolean>> = mutableListOf()
  val savedFilesNames = savedFiles
    .listFiles()?.map { it.name }
  whatsappStatusDir.listFiles()?.forEach {
    when {
      it.name.endsWith(".jpg") || it.name.endsWith(".jpeg") -> {
        images.add(it to (savedFilesNames?.contains(it.name) ?: false))
      }

      it.name.endsWith(".mp4") -> {
        videos.add(it to (savedFilesNames?.contains(it.name) ?: false))
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .safeDrawingPadding(),
    topBar = {
      AppBar(
        canNavigateBack = appState.canNavigateBack,
        currentScreen = appState.selectedScreen,
        navigateUp = appViewModel::navigateBack,
      )
    },
    bottomBar = {
      BottomNavigationBar(appState.selectedScreen, onItemSelect = appViewModel::switchScreen)
    },
  ) {
    NavHost(
      navController = appViewModel.navController,
      startDestination = appState.selectedScreen.name,
      modifier = Modifier.padding(it)
    ) {
      // Image Screen
      composable(route = Screens.Images.name) {
        ImagesScreen(
          modifier = Modifier.fillMaxWidth(),
          files = images,
          onStatusClick = appViewModel::viewStatus
        )
      }
      // Video Screen
      composable(route = Screens.Videos.name) {
        VideosScreen(
          modifier = Modifier.fillMaxWidth(),
          files = videos
        )
      }
      // Saved Screen
      composable(route = Screens.Saved.name) {
        SavedScreen(
          modifier = Modifier.fillMaxWidth(),
          files = savedFiles.listFiles()?.toList() ?: emptyList(),
          onStatusClick = appViewModel::viewStatus
        )
      }
      // Settings Screen
      composable(route = Screens.Settings.name) {
        SettingsScreen(
          modifier = Modifier.fillMaxWidth(),
        )
      }
      // Status View
      composable(
        "${Screens.StatusView.name}/{index}",
        arguments = listOf(navArgument("index") { type = NavType.IntType })
      ) { backStackEntry ->
        val index = backStackEntry.arguments?.getInt("index")
        val files = when (appViewModel.previousScreen()) {
          Screens.Images -> images
          Screens.Videos -> videos
          Screens.Saved -> savedFiles.listFiles()?.map { file -> file to true }?.toList()
            ?: emptyList()

          else -> emptyList()
        }
        StatusPager(startIndex = index ?: 0, files = files)
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
  val dir = "E:\\MY FILES\\Camera"
  AppTheme {
    MainScreen(appViewModel = AppViewModel(navController = rememberNavController()), savedDir = dir, directory = dir)
  }
}
