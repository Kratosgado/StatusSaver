package com.kratosgado.statussaver.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.ui.components.AppBar
import com.kratosgado.statussaver.ui.views.SavedScreen
import com.kratosgado.statussaver.ui.views.SettingsScreen
import com.kratosgado.statussaver.ui.views.StatusGrid
import com.kratosgado.statussaver.ui.views.StatusPager

sealed class Screen(val title: String, val icon: ImageVector) {
  data object Statuses : Screen("Statuses", Icons.Default.PlayArrow)
  data object Saved : Screen("Saved", Icons.Default.CheckCircle)
  data object Settings : Screen("Settings", Icons.Default.Settings)
  data object View : Screen("View", icon = Icons.Default.Edit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  statuses: List<Status>,
  saved: List<Status>,
  onSaveClick: (Status) -> Unit,
  onShareClick: () -> Unit,
  onSendClick: () -> Unit
) {
  var selectedDestination by remember { mutableStateOf<Screen>(Screen.Statuses) }
  var index by remember { mutableStateOf(Pair(0, true)) }
  val destinations = listOf(Screen.Statuses, Screen.Saved, Screen.Settings)

  Scaffold(
    topBar = {
      AppBar(
        currentScreen = selectedDestination,
        canNavigateBack = selectedDestination == Screen.View,
        navigateUp = { selectedDestination = if (index.second) Screen.Statuses else Screen.Saved })
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
        onItemClick = {
          index = it
          selectedDestination = Screen.View
        },
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      )

      Screen.Saved -> SavedScreen(
        modifier = Modifier.padding(innerPadding),
        statuses = saved,
        onSaveClick = onSaveClick,
        onItemClick = {
          index = it
          selectedDestination = Screen.View
        }
      )

      Screen.Settings -> SettingsScreen(modifier = Modifier.padding(innerPadding))
      Screen.View -> StatusPager(
        stats = if (index.second) statuses else saved,
        onSaveClick = onSaveClick,
        startIndex = index.first
      )
    }
  }
}
