package com.kratosgado.statussaver.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kratosgado.statussaver.domain.Status

@Composable
fun StatusGrid(
  modifier: Modifier = Modifier,
  statuses: List<Status>,
  onSaveClick: (Status) -> Unit,
  onItemClick: (Status) -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = modifier,
    contentPadding = PaddingValues(8.dp)
  ) {
    items(statuses.size) { index ->
      val status = statuses[index]
      StatusItem(
        status = status,
        onSaveClick = { onSaveClick(status) },
        onClick = { onItemClick(status) }
      )
    }
  }
}

@Composable
fun StatusItem(
  status: Status,
  onSaveClick: () -> Unit,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .padding(4.dp)
      .clickable { onClick() }
  ) {
    when (status) {
      is Status.Image -> {
        AsyncImage(
          model = status.uri,
          contentDescription = "Status Image",
          modifier = Modifier.aspectRatio(0.75f)
        )
      }

      is Status.Video -> {
        // Use VideoThumbnail composable with ExoPlayer
        Text("Video: ${status.name}")
      }
    }
    IconButton(onClick = onSaveClick) {
      Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Save")
    }
  }
}