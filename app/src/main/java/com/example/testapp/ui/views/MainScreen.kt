package com.example.testapp.ui.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.views.components.AppBar
import com.example.testapp.ui.views.components.BottomNavigationBar
import java.io.File

enum class Screens {
  Images, StatusView, Videos, Saved, Settings
}

@Composable
fun MainScreen(
  directory: String,
  savedDir: String = "",
  navController: NavHostController = rememberNavController()
) {
  var selectedItem by remember { mutableStateOf(Screens.Images) }
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentScreen =
    Screens.valueOf(backStackEntry?.destination?.route?.split('/')?.get(0) ?: Screens.Images.name)

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
        canNavigateBack = navController.previousBackStackEntry != null,
        currentScreen = currentScreen,
        navigateUp = { navController.navigateUp() },
      )
    },
    bottomBar = {
      BottomNavigationBar(selectedItem, onItemSelect = {
        selectedItem = it
        navController.navigate(it.name)
      })
    },
  ) {
    NavHost(
      navController = navController,
      startDestination = Screens.Images.name,
      modifier = Modifier.padding(it)
    ) {
      // Image Screen
      composable(route = Screens.Images.name) {
        ImagesScreen(
          modifier = Modifier.fillMaxWidth(),
          files = images,
          onStatusClick = { idx -> navController.navigate("${Screens.StatusView.name}/$idx") }
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
          onStatusClick = { idx -> navController.navigate("${Screens.StatusView.name}/$idx") }
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
        val prev = Screens.valueOf(
          navController.previousBackStackEntry?.destination?.route ?: Screens.Images.name
        )
        val files = when (prev) {
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
  val dir = "/home/kratosgado/Pictures/Camera/"
  AppTheme {
    MainScreen(dir, dir)
  }
}
