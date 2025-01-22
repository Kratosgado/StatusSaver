package com.kratosgado.statussaver.ui.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kratosgado.statussaver.utils.rememberImageUri
import com.kratosgado.statussaver.utils.saveStatus

@Composable
fun VideosScreen(modifier: Modifier, files: List<Pair<Uri, Boolean>>) {
  val tag = "VideoScreen"
  Log.d(tag, "Video Screens")

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = modifier,
  ) {
    items(files.size) { index ->
      val entry = files[index]
      VideoItem(entry.first, entry.second)
    }
  }
}

@Composable
private fun VideoItem(file: Uri, saved: Boolean = true) {
  val context = LocalContext.current
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(150.dp)
      .padding(2.dp),
  ) {
    Image(
      bitmap = rememberImageUri(file),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
    )
    Icon(
      imageVector = Icons.Outlined.PlayArrow,
      contentDescription = "Video Status",
      modifier = Modifier
        .size(80.dp)
        .align(Alignment.Center)
    )
    IconButton(
      onClick = { saveStatus(file, context) },
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

//@Preview(showBackground = true)
//@Composable
//private fun PreviewVideosScreen() {
//  AppTheme {
//    VideosScreen(
//      modifier = Modifier
//        .padding(8.dp)
//        .fillMaxSize(),
//      directory = "/home/kratosgado/Pictures/Camera/"
//    )
//  }
//}
