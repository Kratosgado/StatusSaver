package com.kratosgado.statussaver.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.ui.components.StatusItem

@Composable
fun SavedScreen(
  modifier: Modifier = Modifier,
  statuses: List<Status>,
  onSaveClick: (Status) -> Unit,
  onItemClick: (Status) -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = modifier,
    contentPadding = PaddingValues(8.dp)
  ) {
    items(statuses.size) { index ->
      val status = statuses[index]
      StatusItem(
        modifier = Modifier
          .padding(2.dp)
          .clickable { onItemClick(status) },
        status = status,
        onSaveClick = { onSaveClick(status) },
//        onClick = { onItemClick(status) }
      )
    }
  }
}
