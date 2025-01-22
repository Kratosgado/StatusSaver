package com.kratosgado.statussaver.ui.views

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kratosgado.statussaver.utils.rememberImageUri
import com.kratosgado.statussaver.utils.saveStatus

@Composable
fun ImagesScreen(
  modifier: Modifier,
  files: List<Pair<Uri, Boolean>>,
  onStatusClick: (Int) -> Unit
) {

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

@Composable
fun ImageItem(
  modifier: Modifier = Modifier,
  contentScale: ContentScale,
  file: Uri,
  saved: Boolean = true
) {
  val context = LocalContext.current

  Box(
    modifier = modifier
  ) {
    Image(
      bitmap = rememberImageUri(file),
      contentDescription = null,
      contentScale = contentScale,
      modifier = Modifier
        .fillMaxSize()
    )
    IconButton(
      onClick = { saveStatus(file, context = context) },
      modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = if (saved) Icons.Default.Check else Icons.Default.CheckCircle,
        contentDescription = "Save",
        tint = if (saved) Green else Color.Red,
        modifier = Modifier
          .size(30.dp)
      )
    }
  }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewImagesScreen() {
//  AppTheme {
//    ImagesScreen(
//      modifier = Modifier
//        .padding(8.dp)
//        .fillMaxSize(),
//      directory = "/home/kratosgado/Pictures/Camera/"
//    )
//  }
//}

@Composable
fun StatusPager(
  modifier: Modifier = Modifier,
  files: List<Pair<Uri, Boolean>>,
  startIndex: Int,
) {
  val pagerState = rememberPagerState(
    initialPage = startIndex,
    initialPageOffsetFraction = 0f,
    pageCount = { files.size }
  )

  Box(
    modifier = modifier
      .background(Color.Black)
  ) {
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
      val (file, saved) = files[page]
      ImageItem(
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
        file = file,
        saved = saved
      )
    }
  }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewImageView() {
//  AppTheme {
//    ViewImage(index = 0, files = listOf(File("/home/kratosgado/Pictures/Camera/first.jpg") to false))
//  }
//}
