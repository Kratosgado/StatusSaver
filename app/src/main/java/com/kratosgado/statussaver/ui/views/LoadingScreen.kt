package com.kratosgado.statussaver.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen(message: String) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = androidx.compose.ui.Alignment.Center
  ) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
      CircularProgressIndicator()
      Spacer(modifier = Modifier.height(16.dp))
      Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
  }
}