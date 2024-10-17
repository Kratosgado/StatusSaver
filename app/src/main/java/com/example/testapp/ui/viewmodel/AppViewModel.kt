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

  fun navigateBack(){
    navController.navigateUp()
    _uiState.update {
      it.copy(selectedScreen = Screens.valueOf(navController.currentDestination?.route ?: Screens.Images.name))
    }
  }

  fun viewStatus(index: Int){
    navController.navigate("${Screens.StatusView.name}/$index")
    _uiState.update {
      it.copy(selectedScreen = Screens.StatusView, canNavigateBack = true)
    }
  }

  fun previousScreen(): Screens {
    return Screens.valueOf(navController.previousBackStackEntry?.destination?.route ?: Screens.Images.name)
  }
}