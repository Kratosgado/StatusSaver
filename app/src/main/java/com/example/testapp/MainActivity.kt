package com.example.testapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.testapp.ui.theme.AppTheme
import com.example.testapp.ui.views.MainScreen

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
    val storage = Environment.getExternalStorageDirectory()
    val savedDir = "/StatusSaver/"
    if (!storage.resolve(savedDir).exists()) {
      storage.resolve(savedDir).mkdir()
    }


    setContent {
      AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainScreen(
            savedDir = storage.absolutePath + savedDir,
            directory = storage.absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/"
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
        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
      ),
      REQUEST_PERMISSION_CODE,
    )
  }
}
