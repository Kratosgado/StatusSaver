package com.kratosgado.statussaver.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream

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

fun saveStatus(uri: Uri, context: Context) {
  try {
    Log.d("Saving", "Saving")
    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(uri)

    if (inputStream != null) {
      if (isExternalStorageWritable()) Log.d("Saving", "Writable")
      val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "StatusSaver"
      )
      if (!directory.exists()) {
        Log.d("Saving", "creating directory")
        directory.mkdirs()
      }
      val file = DocumentFile.fromSingleUri(context, uri)
      val newFile = File(directory, file?.name ?: "default.jpg")
      if (newFile.exists()) throw Exception("File has been already saved")
      FileOutputStream(newFile).use {
        inputStream.copyTo(it)
        inputStream.close()
      }
      Log.d("StatusItem", "File saved as ${newFile.path}")
      Toast.makeText(context, "Saved: ${newFile.path}", Toast.LENGTH_SHORT).show()
      return
    }
    throw Exception("Cannot open file")
  } catch (e: Exception) {
    Log.e("SaveStatus", e.message, e.fillInStackTrace())
    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//    TODO("Not yet implemented: Saving status error")
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