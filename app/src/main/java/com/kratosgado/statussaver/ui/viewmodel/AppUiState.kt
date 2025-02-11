package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import com.kratosgado.statussaver.domain.Status
import java.io.File


data class AppUiState(
  val statuses: Map<String, Status> = emptyMap(),
  val saved: Map<String, Status> = emptyMap(),
  val error: String? = null,
  val statusDirUri: Uri? = null,
  val savedDirUri: File? = null,
)