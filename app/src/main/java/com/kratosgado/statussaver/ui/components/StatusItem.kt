package com.kratosgado.statussaver.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.domain.StatusType


@Composable
fun StatusItem(
  modifier: Modifier,
  contentScale: ContentScale = ContentScale.Crop,
  status: Status,
  onSaveClick: () -> Unit,
  onPlay: (Uri) -> Unit
) {
  Box(modifier = Modifier.aspectRatio(0.75f)) {
    when (status.type) {
      StatusType.Image ->
        Card(modifier) {
          AsyncImage(
            model = status.uri,
            contentDescription = "Status Image",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
          )
        }

      StatusType.Video -> {
        val model = ImageRequest.Builder(LocalContext.current).data(status.uri)
          .videoFrameMillis(10000)
          .decoderFactory { res, opt, _ -> VideoFrameDecoder(res.source, opt) }.build()
        Card(modifier) {
          AsyncImage(
            model = model,
            contentScale = contentScale,
            contentDescription = "Video thumbnail",
            modifier = Modifier.fillMaxSize()
          )
        }
        IconButton(onClick = { onPlay(status.uri) }) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier
              .align(Alignment.Center)
              .size(80.dp)
          )
        }
      }
    }
    IconButton(
      onClick = onSaveClick, modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        tint = if (status.isSaved) Green else LocalContentColor.current,
        contentDescription = "Save"
      )
    }
  }
}