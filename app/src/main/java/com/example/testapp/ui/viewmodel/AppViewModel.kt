package com.example.testapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.testapp.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(val  navController: NavHostController
): ViewModel() {
  // ui state
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  // handle events
  fun switchScreen(screen: Screens) {
    _uiState.update {
      it.copy(selectedScreen = screen)
    }
    navController.navigate(screen.name)
  }

}