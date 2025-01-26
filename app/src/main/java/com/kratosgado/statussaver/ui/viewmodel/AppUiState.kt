package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import com.kratosgado.statussaver.domain.Status


data class AppUiState(
  val statuses: List<Status> = emptyList(),
  val isLoading: Boolean = true,
  val error: String? = null,
  val saveDirUri: Uri? = null
)