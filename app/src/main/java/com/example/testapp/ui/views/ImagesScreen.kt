package com.example.testapp.ui.views

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.testapp.ui.theme.Green
import com.example.testapp.utils.rememberImageBitmap
import com.example.testapp.utils.saveStatus
import java.io.File

@Composable
fun ImagesScreen(modifier: Modifier, directory: String) {
  val tag = "StatusScreen"
  Log.d(tag, "Status Screens")

  val whatsappStatusDir = File(Environment.getExternalStorageDirectory(), directory)
//  val whatsappStatusDir = File(directory) // during preview
  Log.d(tag, "$whatsappStatusDir : ${whatsappStatusDir.exists()}")

  // get saved files
  val savedFiles = File(Environment.getExternalStorageDirectory(), "/StatusSaver")
    .listFiles()?.map { it.name }
  // get the list of status files
  val files =
    whatsappStatusDir.listFiles()?.filter {
      it.name.endsWith(".jpg") || it.name.endsWith(".jpeg")
    }?.map {
      it to (savedFiles?.contains(it.name) ?: false)
    }
      ?: emptyList()

  var viewImage by remember {
    mutableStateOf(false to 0)
  }

  when (viewImage.first) {
    false -> LazyVerticalGrid(
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
            .clickable { viewImage = true to index }
            .padding(2.dp),
        )
      }
    }

    true -> {
      ImagePager(modifier = modifier, files = files, startIndex = viewImage.second) {
        viewImage = false to 0
      }
    }
  }
}

@Composable
private fun ImageItem(
  modifier: Modifier = Modifier,
  contentScale: ContentScale,
  file: File,
  saved: Boolean = true
) {

  Box(
    modifier = modifier
  ) {
    Image(
      bitmap = rememberImageBitmap(file = file).asImageBitmap(),
      contentDescription = null,
      contentScale = contentScale,
      modifier = Modifier
        .fillMaxSize()
    )
    IconButton(
      onClick = { saveStatus(file) },
      modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = if (saved) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePager(
  modifier: Modifier = Modifier,
  files: List<Pair<File, Boolean>>,
  startIndex: Int,
  OnDismiss: () -> Unit
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
    IconButton(
      onClick = { OnDismiss() },
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Close",
        tint = Color.White,
        modifier = Modifier.size(30.dp)
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
