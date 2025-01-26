package com.kratosgado.statussaver.ui
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.safeDrawingPadding
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.kratosgado.statussaver.data.StatusRepository
//import com.kratosgado.statussaver.ui.components.AppBar
//import com.kratosgado.statussaver.ui.components.BottomNavigationBar
//import com.kratosgado.statussaver.ui.theme.AppTheme
//import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
//import com.kratosgado.statussaver.ui.views.ImagesScreen
//import com.kratosgado.statussaver.ui.views.SavedScreen
////import com.kratosgado.statussaver.ui.views.SettingsScreen
//import com.kratosgado.statussaver.ui.views.StatusPager
//import com.kratosgado.statussaver.ui.views.VideosScreen
//
//
//@Composable
//fun MainScreen(
//  appViewModel: AppViewModel = viewModel(),
//) {
//  val appState by appViewModel.uiState.collectAsState()
//
//  Scaffold(
//    modifier = Modifier
//      .fillMaxSize()
//      .safeDrawingPadding(),
//    topBar = {
//      AppBar(
//        canNavigateBack = appState.canNavigateBack,
//        currentScreen = appState.selectedScreen,
//        navigateUp = appViewModel::navigateBack,
//      )
//    },
//    bottomBar = {
//      BottomNavigationBar(appState.selectedScreen, onItemSelect = appViewModel::switchScreen)
//    },
//  ) {
//    NavHost(
//      navController = appViewModel.navController,
//      startDestination = appState.selectedScreen.name,
//      modifier = Modifier.padding(it)
//    ) {
//      // Image Screen
//      composable(route = Screens.Images.name) {
//        ImagesScreen(
//          modifier = Modifier.fillMaxWidth(),
//          files = remember { appState.images },
//          onStatusClick = appViewModel::viewStatus,
//          onClickSave = appViewModel::saveStatus,
//        )
//      }
//      // Video Screen
//      composable(route = Screens.Videos.name) {
//        VideosScreen(
//          modifier = Modifier.fillMaxWidth(),
//          files = remember { appViewModel.images },
//          onClickSave = appViewModel::saveStatus,
//        )
//      }
//      // Saved Screen
//      composable(route = Screens.Saved.name) {
//        SavedScreen(
//          modifier = Modifier.fillMaxWidth(),
//          files = remember { appViewModel.saved },
//          onStatusClick = appViewModel::viewStatus
//        )
//      }
//      // Settings Screen
//      composable(route = Screens.Settings.name) {
//        SettingsScreen(
//          modifier = Modifier.fillMaxWidth(),
//        )
//      }
//      // Status View
//      composable(
//        route = "${Screens.StatusView.name}/{index}",
//        arguments = listOf(navArgument("index") { type = NavType.IntType })
//      ) { backStackEntry ->
//        val index = backStackEntry.arguments?.getInt("index")
//        val files = when (appViewModel.previousScreen()) {
//          Screens.Videos -> appViewModel.videos
//          Screens.Saved -> appViewModel.saved
//          else -> appViewModel.images
//        }
//        StatusPager(startIndex = index ?: 0, files = files, onClickSave = appViewModel::saveStatus)
//      }
//    }
//  }
//}
