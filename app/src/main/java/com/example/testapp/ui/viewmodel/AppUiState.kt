package com.example.testapp.ui.viewmodel

import com.example.testapp.ui.Screens


data class AppUiState(
  val selectedScreen: Screens = Screens.Images,
  val canNavigateBack: Boolean = false,
)