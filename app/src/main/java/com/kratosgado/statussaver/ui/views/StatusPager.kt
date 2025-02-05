package com.kratosgado.statussaver.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.kratosgado.statussaver.R
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
  onRepost: (Status) -> Unit = {}
) {
  var currentVideoUri by remember { mutableStateOf<Uri?>(null) }

  val pagerState = rememberPagerState(
    initialPage = startIndex,
    initialPageOffsetFraction = 0f,
    pageCount = { stats.size }
  )
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Status View") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
          IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back button") }
        }
      )
    },
    bottomBar = {
      NavigationBar {
        NavigationBarItem(
          icon = {
            Icon(
              Icons.Default.Refresh,
              contentDescription = stringResource(R.string.repost)
            )
          },
          label = { Text(stringResource(R.string.repost)) },
          selected = false,
          onClick = { onRepost(stats[pagerState.currentPage]) }
        )
        NavigationBarItem(
          icon = {
            Icon(Icons.Default.Share, stringResource(R.string.share))
          },
          label = { Text(stringResource(R.string.share)) },
          selected = false,
          onClick = { onShare(stats[pagerState.currentPage]) }
        )
        NavigationBarItem(
          icon = {
            Icon(
              Icons.Default.CheckCircle,
              tint = if (stats[pagerState.currentPage].isSaved) Color.Cyan else LocalContentColor.current,
              contentDescription = stringResource(R.string.save)
            )
          },
          label = { Text(stringResource(R.string.save)) },
          selected = false,
          onClick = { onSaveClick(stats[pagerState.currentPage]) }
        )
      }
    }
  ) { innerPadding ->


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
