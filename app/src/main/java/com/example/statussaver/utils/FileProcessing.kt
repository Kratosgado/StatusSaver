package com.example.statussaver.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
fun rememberImageBitmap(file: File): Bitmap {
  return remember { BitmapFactory.decodeFile(file.absolutePath) }
}

@Composable
fun rememberImageUri(uri: Uri): ImageBitmap {
  val context = LocalContext.current;
  return remember(uri) {
    context.contentResolver.openFileDescriptor(uri, "r")?.use {
      val fileDescriptor = it.fileDescriptor
      BitmapFactory.decodeFileDescriptor(fileDescriptor).asImageBitmap()
    } ?: throw IllegalArgumentException("Unable to load Image")
  }
}

fun saveStatus(file: Uri) {
  try {
    val directory = File(Environment.getExternalStorageDirectory(), "StatusSaver")
    if (!directory.exists()) directory.mkdirs()
//    val newFile = File(directory, file.name)
//    file.copyTo(newFile)
//    Log.d("StatusItem", "File saved as ${newFile.path}")
  } catch (e: Exception) {
    TODO("Not yet implemented: Saving status error")
  }
}