package com.kratosgado.statussaver.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.domain.StatusType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusPager(
  stats: List<Status>,
  startIndex: Int,
  onSaveClick: (Status) -> Unit,
  onBack: () -> Unit = {},
  onShare: (Status) -> Unit = {},
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Status View") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
      )
    },
    bottomBar = {
    }
  ) { innerPadding ->
    var currentVideoUri by remember { mutableStateOf<Uri?>(null) }

    val pagerState = rememberPagerState(
      initialPage = startIndex,
      initialPageOffsetFraction = 0f,
      pageCount = { stats.size }
    )

    Box(
      modifier = Modifier
        .padding(innerPadding)
//        .background(Color.Black)
    ) {
      HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
        val stat = stats[page]
        when (stat.type) {
          StatusType.Image -> AsyncImage(
            model = stat.uri,
            contentDescription = "Status Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
          )

          StatusType.Video -> VideoPlayerScreen(
            videoUri = stat.uri,
            onBackPressed = { currentVideoUri = null }
          )
        }
      }
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
