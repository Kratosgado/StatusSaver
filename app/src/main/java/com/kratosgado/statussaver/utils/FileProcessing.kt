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


// for read and write.
fun isExternalStorageWritable(): Boolean {
  return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}
