package com.example.testapp.ui.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.views.Screens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
  modifier: Modifier = Modifier,
  canNavigateBack: Boolean = false,
  currentScreen: Screens,
  navigateUp: () -> Unit,
) {
  TopAppBar(
    title = { Text(currentScreen.name) },
    colors = TopAppBarDefaults.mediumTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    ),
    modifier = modifier,
    navigationIcon = {
      if (canNavigateBack) {
        IconButton(onClick = navigateUp) {
          Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
        }
      }
    },
    actions = {
      IconButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.Send, contentDescription = "Share")
      }
      IconButton(onClick = { /*TODO*/ }) {
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
      canNavigateBack = false,
      currentScreen = Screens.Settings,
      navigateUp = {},
      modifier = Modifier
    )
  }
}