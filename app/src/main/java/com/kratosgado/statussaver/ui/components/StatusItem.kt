package com.kratosgado.statussaver.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kratosgado.statussaver.domain.Status


@Composable
fun StatusItem(
  modifier: Modifier,
  contentScale: ContentScale = ContentScale.Crop,
  status: Status,
  onSaveClick: () -> Unit,
) {
  Box(modifier = Modifier.aspectRatio(0.75f)) {
    Card(modifier) {
      when (status) {
        is Status.Image -> {
          AsyncImage(
            model = status.uri,
            contentDescription = "Status Image",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
          )
        }

        is Status.Video -> {

          // Use VideoThumbnail composable with ExoPlayer
          Text("Video: ${status.name}", modifier = Modifier.fillMaxSize())
        }
      }
    }
    IconButton(
      onClick = onSaveClick, modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        tint = if (status.saved) Green else LocalContentColor.current,
        contentDescription = "Save"
      )
    }
  }
}

@Composable
fun StatusItemOwn(
  modifier: Modifier = Modifier,
  contentScale: ContentScale,
  status: Status,
  onClickSave: () -> Unit
) {

  Box(
    modifier = modifier
  ) {
    when (status) {
      is Status.Image -> AsyncImage(
        model = status.uri,
        contentDescription = null,
        contentScale = contentScale,
        modifier = Modifier
          .fillMaxSize()
      )

      is Status.Video -> Text("Video will be display: ${status.name}")
    }

    IconButton(
      onClick = onClickSave,
      modifier = Modifier
        .align(Alignment.BottomEnd)
    ) {
      Icon(
        imageVector = if (false) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
        contentDescription = "Save",
        tint = if (false) Green else Color.Red,
        modifier = Modifier
          .size(30.dp)
      )
    }
  }
}