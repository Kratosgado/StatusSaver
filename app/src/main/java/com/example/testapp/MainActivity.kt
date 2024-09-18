package com.example.testapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.status.StatusesScreen
import com.example.testapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
  companion object {
    private const val TAG = "MainActivity"
    private const val REQUEST_PERMISSION_CODE = 100
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    Log.d(TAG, "Starting Application: checking permissions")
    checkPermissions()
    setContent {
      AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          StatusesScreen(
            modifier = Modifier
              .fillMaxSize()
              .padding(8.dp), "Android/media/com.whatsapp/WhatsApp/Media/.Statuses/"
          )
        }
      }
    }
  }


  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_PERMISSION_CODE) {
      if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
        //
      } else {
        Log.d("Permissions", "${grantResults.find { it != PackageManager.PERMISSION_GRANTED }}")
      }
    }
  }

  private fun checkPermissions() {
    if (!hasPermissions()) {
      requestPermissions()
    }
  }

  private fun hasPermissions(): Boolean {
    return hasReadPermissions() && hasWritePermissions() && hasAccessToHiddenFiles()
  }

  private fun hasAccessToHiddenFiles(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      checkSelfPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    } else {
      TODO("VERSION.SDK_INT < R")
    }
  }

  private fun hasReadPermissions(): Boolean {
    return checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  private fun hasWritePermissions(): Boolean {
    return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestPermissions() {
    requestPermissions(
      arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission_group.STORAGE
      ),
      REQUEST_PERMISSION_CODE,
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
  AppTheme {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { AppBar() },
      bottomBar = { BottomBar() },
      floatingActionButton = { Text(text = "Hello") }
    ) {
      StatusesScreen(
        modifier = Modifier.padding(top = it.calculateTopPadding()),
        directory = "/home/kratosgado/Pictures/Camera/"
      )
    }
  }
}

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

@Composable
fun BottomBar() {
  NavigationBar() {
    Text(text = "Hello")
    Text(text = "what")
  }

}