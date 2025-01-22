package com.kratosgado.statussaver.utils

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberImageUri(uri: Uri): ImageBitmap {
  val context = LocalContext.current
  return remember(uri) {
    context.contentResolver.openFileDescriptor(uri, "r")?.use {
      val fileDescriptor = it.fileDescriptor
      BitmapFactory.decodeFileDescriptor(fileDescriptor).asImageBitmap()
    } ?: throw IllegalArgumentException("Unable to load Image")
  }
}


// Checks if a volume containing external storage is available
// for read and write.
fun isExternalStorageWritable(): Boolean {
  return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

// Checks if a volume containing external storage is available to at least read.
fun isExternalStorageReadable(): Boolean {
  return Environment.getExternalStorageState() in
      setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}