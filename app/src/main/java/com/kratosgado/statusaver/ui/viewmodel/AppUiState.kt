package com.kratosgado.statusaver.ui.viewmodel

import android.net.Uri
import com.kratosgado.statusaver.domain.Status
import java.io.File


data class AppUiState(
  val statuses: Map<String, Status> = emptyMap(),
  val saved: Map<String, Status> = emptyMap(),
  val error: String? = null,
  val statusDirUri: Uri? = null,
  val savedDirUri: File? = null,
)