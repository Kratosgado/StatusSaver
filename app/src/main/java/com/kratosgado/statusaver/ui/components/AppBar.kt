package com.kratosgado.statusaver.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kratosgado.statusaver.ui.Screen
import com.kratosgado.statusaver.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
  modifier: Modifier = Modifier,
  currentScreen: Screen,
  onShareClick: () -> Unit
) {
  TopAppBar(
    title = { Text(currentScreen.title) },
    colors = TopAppBarDefaults.mediumTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    modifier = modifier,
    actions = {
      IconButton(onClick = onShareClick) {
        Icon(imageVector = Icons.Default.Send, contentDescription = "Share")
      }
      IconButton(onClick = onShareClick) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
      }
    }
  )
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
  AppTheme {
    AppBar(
      currentScreen = Screen.Statuses,
      modifier = Modifier,
      onShareClick = {}
    )
  }
}