package com.example.testapp.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.theme.AppTheme
import java.io.File

@Composable
fun ImagesScreen(modifier: Modifier, directory: String) {
  val tag = "StatusScreen"
  Log.d(tag, "Status Screens")

//  val whatsappStatusDir = File(Environment.getExternalStorageDirectory(), directory)
  val whatsappStatusDir = File(directory) // during preview
  Log.d(tag, "$whatsappStatusDir : ${whatsappStatusDir.exists()}")

  // get the list of status files
  val files =
    whatsappStatusDir.listFiles()?.filter {
      it.name.endsWith(".jpg") || it.name.endsWith(".jpeg")
    }
      ?: emptyList()
//  val savedFiles = File(Environment.getExternalStorageDirectory(), "StatusSaver")

  Log.d(tag, "length: ${files.size}")

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = modifier,
  ) {
    items(files.size) { index ->
      val file = files[index]
      ImageItem(file = file)
    }
  }
}

@Composable
private fun ImageItem(file: File, saved: Boolean = true) {

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

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(150.dp)
      .padding(2.dp),
  ) {
    Image(
      bitmap = rememberImageBitmap(file = file).asImageBitmap(),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
    )
    IconButton(
      onClick = { saveStatus(file) },
      modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = if (saved) Icons.Default.CheckCircle else Icons.Default.Check,
        contentDescription = "Save",
        modifier = Modifier
          .size(24.dp)
          .background(color = if (saved) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error)
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun StatusScreenPreview() {
  AppTheme {
    ImagesScreen(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxSize(),
      directory = "/home/kratosgado/Pictures/Camera/"
    )
  }
}

@Composable
private fun rememberImageBitmap(file: File): Bitmap {
  return remember { BitmapFactory.decodeFile(file.absolutePath) }
}
