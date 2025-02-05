package com.kratosgado.statussaver.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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

sealed class Screen(val title: String, val icon: ImageVector) {
  data object Statuses : Screen("Statuses", Icons.Default.PlayArrow)
  data object Saved : Screen("Saved", Icons.Default.CheckCircle)
  data object Settings : Screen("Settings", Icons.Default.Settings)
}

//@OptIn(ExperimentalMaterial3Api::class)
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
}
