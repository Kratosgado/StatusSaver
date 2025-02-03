package com.kratosgado.statussaver.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kratosgado.statussaver.ui.Screen
import com.kratosgado.statussaver.ui.theme.AppTheme


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
fun PreviewAppBar() {
  AppTheme {
    AppBar(
      currentScreen = Screen.Statuses,
      modifier = Modifier,
      onShareClick = {}
    )
  }
}