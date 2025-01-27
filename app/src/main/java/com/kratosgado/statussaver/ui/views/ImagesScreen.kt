package com.kratosgado.statussaver.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.ui.components.StatusItem


@Composable
fun StatusPager(
  modifier: Modifier = Modifier,
  stats: List<Status>,
  startIndex: Int,
  onSaveClick: (Status) -> Unit
) {
  val pagerState = rememberPagerState(
    initialPage = startIndex,
    initialPageOffsetFraction = 0f,
    pageCount = { stats.size }
  )

  Box(
    modifier = modifier
      .background(Color.Black)
  ) {
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
      val stat = stats[page]
      StatusItem(
        status = stat,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
        onSaveClick = { onSaveClick(stat) },
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
