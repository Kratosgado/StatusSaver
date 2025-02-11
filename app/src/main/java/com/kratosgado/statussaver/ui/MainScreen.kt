package com.kratosgado.statussaver.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.ui.components.AppBar
import com.kratosgado.statussaver.ui.views.SavedScreen
import com.kratosgado.statussaver.ui.views.SettingsScreen
import com.kratosgado.statussaver.ui.views.StatusGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class Screen(val title: String, val icon: ImageVector) {
  data object Statuses : Screen("Statuses", Icons.Default.PlayArrow)
  data object Saved : Screen("Saved", Icons.Default.CheckCircle)
  data object Settings : Screen("Settings", Icons.Default.Settings)
}

//@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
  statuses: List<Status>,
  saved: List<Status>,
  onSaveClick: (Status) -> Unit,
  onShareClick: () -> Unit,
  onItemClick: (Pair<Int, Boolean>) -> Unit,
) {
  var selectedDestination by remember { mutableStateOf<Screen>(Screen.Statuses) }
  val destinations = listOf(Screen.Statuses, Screen.Saved, Screen.Settings)
  var refreshing by remember { mutableStateOf(false) }

  // Coroutine scope for handling refresh
  val scope = rememberCoroutineScope()

  // Sample list of items
  var items by remember { mutableStateOf(List(20) { "Item ${it + 1}" }) }

  // Function to handle refresh
  fun refresh() = scope.launch {
    refreshing = true
    // Simulate network delay
    delay(2000)
    // Update items
    items = List(20) { "Refreshed Item ${it + 1}" }
    refreshing = false
  }

  // Create pull refresh state
  val pullRefreshState = rememberPullRefreshState(
    refreshing = refreshing,
    onRefresh = ::refresh
  )
  Box() {
    Scaffold(
      topBar = {
        AppBar(
          currentScreen = selectedDestination,
          onShareClick = onShareClick,
        )
      },
      bottomBar = {
        NavigationBar {
          destinations.forEach { screen ->
            NavigationBarItem(
              icon = { Icon(screen.icon, contentDescription = screen.title) },
              label = { Text(screen.title) },
              selected = selectedDestination == screen,
              onClick = { selectedDestination = screen }
            )
          }
        }
      }
    ) { innerPadding ->
      when (selectedDestination) {
        Screen.Statuses -> StatusGrid(
          statuses = statuses,
          onSaveClick = onSaveClick,
          onItemClick = onItemClick,
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        )

        Screen.Saved -> SavedScreen(
          modifier = Modifier.padding(innerPadding),
          statuses = saved,
          onSaveClick = onSaveClick,
          onItemClick = onItemClick
        )

        Screen.Settings -> SettingsScreen(modifier = Modifier.padding(innerPadding))
      }
    }
    // Pull refresh indicator
    PullRefreshIndicator(
      refreshing = refreshing,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter)
    )
  }
}
