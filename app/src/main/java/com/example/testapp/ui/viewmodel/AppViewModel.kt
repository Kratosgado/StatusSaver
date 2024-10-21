package com.example.testapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.testapp.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class AppViewModel(
  val navController: NavHostController,
  statusDir: String,
  saveDir: String
): ViewModel() {
  // ui state
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  val images: MutableList<Pair<File, Boolean>> = mutableListOf()
  val videos: MutableList<Pair<File, Boolean>> = mutableListOf()
  val saved: MutableList<Pair<File, Boolean>> = mutableListOf()

  init {
    val whatsappStatusDir = File(statusDir)
    val savedFiles = File(saveDir)

    val savedFilesNames = savedFiles
      .listFiles()?.map {
        saved.add(it to true )
        it.name
      }
    whatsappStatusDir.listFiles()?.forEach {
      when {
        it.name.endsWith(".jpg") || it.name.endsWith(".jpeg") -> {
          images.add(it to (savedFilesNames?.contains(it.name) ?: false))
        }

        it.name.endsWith(".mp4") -> {
          videos.add(it to (savedFilesNames?.contains(it.name) ?: false))
        }
      }
    }
  }

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