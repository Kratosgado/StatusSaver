package com.example.statussaver.ui.viewmodel

import com.example.statussaver.ui.Screens


data class AppUiState(
  val selectedScreen: Screens = Screens.Images,
  val canNavigateBack: Boolean = false,
)