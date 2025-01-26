package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import android.os.Environment
import com.kratosgado.statussaver.domain.Status


data class AppUiState(
  val statuses: List<Status> = emptyList(),
  val saved: List<Status> = emptyList(),
  val isLoading: Boolean = true,
  val error: String? = null,
  val statusDirUri: Uri? = null,
  val savedDirUri: Uri = Uri.fromFile(Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DCIM}/StatusSaver"))
)