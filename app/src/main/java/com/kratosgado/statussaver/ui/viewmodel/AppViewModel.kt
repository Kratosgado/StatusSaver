package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kratosgado.statussaver.logic.StatusRepo
import com.kratosgado.statussaver.ui.Screens
import com.kratosgado.statussaver.utils.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class AppViewModel(
  private val statusRepo: StatusRepo,
//  savedStateHandle: SavedStateHandle,
  val navController: NavHostController,
) : ViewModel() {

  // ui state
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()
  private val scope = viewModelScope

  val images: MutableList<Pair<Uri, Boolean>> = mutableStateListOf()
  val videos: MutableList<Pair<Uri, Boolean>> = mutableStateListOf()
  val saved: MutableList<Pair<Uri, Boolean>> = mutableStateListOf()

  init {
    loadStatus()
  }

  private fun log(v: String) {
    Log.d("AppViewModel", v)
  }

  private fun loadStatus() {
    scope.launch(Dispatchers.IO) {
      try {
        val status = statusRepo.loadStatuses()
        log("Loaded")
        _uiState.update {
          it.copy(
            images = status.first,
            videos = status.second
//            savedStatuses = savedStats
          )
        }
        log("update ui")
      } catch (e: IOException) {
        _uiState.update { it.copy(error = "Failed to load statuses") }
      }

    }
  }

  private fun updateStatuses(statuses: List<Status>) {
    _uiState.update { it.copy(images = statuses) }
  }

  // handle events
  fun switchScreen(screen: Screens) {
    navController.navigate(screen.name)
    _uiState.update {
      it.copy(selectedScreen = screen)
    }
  }

  fun navigateBack() {
    navController.popBackStack()
    _uiState.update {
      it.copy(
        selectedScreen = Screens.valueOf(
          navController.currentDestination?.route ?: Screens.Images.name,
        ),
        canNavigateBack = false
      )
    }
  }

  fun viewStatus(index: Int) {
    navController.navigate("${Screens.StatusView.name}/$index")
//    navController.navigate("${Screens.StatusView.name}")
    _uiState.update {
      it.copy(selectedScreen = Screens.Images, canNavigateBack = true)
    }
  }

  fun previousScreen(): Screens {
    return Screens.valueOf(
      navController.previousBackStackEntry?.destination?.route ?: Screens.Images.name
    )
  }

  fun saveStatus(uri: Uri) {
    scope.launch(Dispatchers.IO) {
      try {
        statusRepo.saveStatus(uri)
        loadStatus()
      } catch (e: IOException) {
        _uiState.update { it.copy(error = "Failed to save status") }
      }
//      try {
//
//        } else throw Exception("Cannot open file")
//      } catch (e: Exception) {
//        Log.e("SaveStatus", e.message, e.fillInStackTrace())
//        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//      }
    }
  }
}