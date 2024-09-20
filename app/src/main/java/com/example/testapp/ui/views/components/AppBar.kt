package com.example.testapp.ui.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.theme.AppTheme


@Composable
fun AppBar() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(50.dp)
      .background(MaterialTheme.colorScheme.primary),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = "Status Saver")
    Row {
      IconButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.Send, contentDescription = "Share")
      }
      IconButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppBar() {
  AppTheme {
    AppBar()
  }
}