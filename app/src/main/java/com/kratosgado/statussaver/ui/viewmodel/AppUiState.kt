package com.kratosgado.statussaver.ui.viewmodel

import com.kratosgado.statussaver.ui.Screens
import com.kratosgado.statussaver.utils.Status


data class AppUiState(
  val selectedScreen: Screens = Screens.Images,
  val canNavigateBack: Boolean = false,
  val statuses: List<Status> = emptyList(),
  val savedStatuses: List<Status> = emptyList(),
  val error: String? = null
)