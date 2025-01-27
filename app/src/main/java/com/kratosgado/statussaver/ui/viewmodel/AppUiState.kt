package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import com.kratosgado.statussaver.domain.Status
import java.io.File


data class AppUiState(
  val statuses: List<Status> = emptyList(),
  val saved: List<Status> = emptyList(),
  val isLoading: Boolean = true,
  val error: String? = null,
  val statusDirUri: Uri? = null,
  val savedDirUri: File? = null,
)