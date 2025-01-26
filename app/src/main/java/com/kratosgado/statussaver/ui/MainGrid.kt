package com.kratosgado.statussaver.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.ui.views.StatusGrid

sealed class Screen(val title: String, val icon: ImageVector) {
  object Statuses : Screen("Statuses", Icons.Default.PlayArrow)
  object Saved : Screen("Saved", Icons.Default.CheckCircle)
  object Settings : Screen("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGrid(
  statuses: List<Status>,
  onSaveClick: (Status) -> Unit,
  onItemClick: (Status) -> Unit,
  onShareClick: () -> Unit,
  onSendClick: () -> Unit
) {
  var selectedDestination by remember { mutableStateOf<Screen>(Screen.Statuses) }
  val destinations = listOf(Screen.Statuses, Screen.Saved, Screen.Settings)

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(selectedDestination.title) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        actions = {
          IconButton(onClick = onShareClick) {
            Icon(Icons.Default.Share, contentDescription = "Share")
          }
          IconButton(onClick = onSendClick) {
            Icon(Icons.Default.Send, contentDescription = "Send")
          }
        }
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

      Screen.Saved -> SavedScreen(modifier = Modifier.padding(innerPadding))
      Screen.Settings -> SettingsScreen(modifier = Modifier.padding(innerPadding))
    }
  }
}

@Composable
fun SavedScreen(modifier: Modifier = Modifier) {
  // Implement your saved items screen
  Text("Saved Items Screen", modifier = modifier.fillMaxSize())
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
  // Implement your settings screen
  Text("Settings Screen", modifier = modifier.fillMaxSize())
}