package com.example.statussaver.ui.views

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun SavedScreen(modifier: Modifier, files: List<Pair<Uri, Boolean>>, onStatusClick: (Int) -> Unit) {
  val tag = "SavedScreen"
  Log.d(tag, "Saved Screens")

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = modifier,
  ) {
    items(files.size) { index ->
      val (file, saved) = files[index]
      ImageItem(
        file = file, saved = saved,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .clickable { onStatusClick(index) }
          .padding(2.dp),
      )
    }
  }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewSavedScreen() {
//  AppTheme {
//    SavedScreen(
//      modifier = Modifier
//        .padding(8.dp)
//        .fillMaxSize(),
//      directory = "/home/kratosgado/Pictures/Camera/"
//    )
//  }
//}
