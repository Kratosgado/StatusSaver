package com.example.statussaver.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File

@Composable
fun rememberImageBitmap(file: File): Bitmap {
  return remember { BitmapFactory.decodeFile(file.absolutePath) }
}

fun saveStatus(file: File) {
  try {
    val directory = File(Environment.getExternalStorageDirectory(), "StatusSaver")
    if (!directory.exists()) directory.mkdirs()
    val newFile = File(directory, file.name)
    file.copyTo(newFile)
    Log.d("StatusItem", "File saved as ${newFile.path}")
  } catch (e: Exception) {
    TODO("Not yet implemented: Saving status error")
  }
}