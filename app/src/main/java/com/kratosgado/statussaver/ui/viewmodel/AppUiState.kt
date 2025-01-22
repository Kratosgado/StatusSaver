package com.kratosgado.statussaver.ui.viewmodel

import com.kratosgado.statussaver.ui.Screens


data class AppUiState(
  val selectedScreen: Screens = Screens.Images,
  val canNavigateBack: Boolean = false,
)