package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
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

  private fun loadStatus() {
    scope.launch(Dispatchers.IO) {
      try {
        val status = statusRepo.loadStatuses()
//        val savedStats = statusRepo.loadStatuses()
        _uiState.update {
          it.copy(
            statuses = status,
//            savedStatuses = savedStats
          )
        }
      } catch (e: IOException) {
        _uiState.update { it.copy(error = "Failed to load statuses") }
      }

    }
  }

  private fun updateStatuses(statuses: List<Status>) {
    _uiState.update { it.copy(statuses = statuses) }
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
//        statusRepo.saveStatus(uri)
        loadStatus()
      } catch (e: IOException) {
        _uiState.update { it.copy(error = "Failed to save status") }
      }
//      try {
//        Log.d("Saving", "Saving")
//        val resolver = context.contentResolver
//        val inputStream = resolver.openInputStream(uri)
//
//        if (inputStream != null) {
//          if (isExternalStorageWritable()) Log.d("Saving", "Writable")
//          val directory = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
//            "StatusSaver"
//          )
//          if (!directory.exists()) {
//            Log.d("Saving", "creating directory")
//            directory.mkdirs()
//          }
//          val file = DocumentFile.fromSingleUri(context, uri)
//          val newFile = File(directory, file?.name ?: "default.jpg")
//          if (newFile.exists()) throw Exception("File has been already saved")
//          FileOutputStream(newFile).use {
//            inputStream.copyTo(it)
//            inputStream.close()
//          }
//          val index = images.indexOfFirst { it.first == uri }
//          images[index] = uri to true
//          Log.d("StatusItem", "File saved as ${newFile.path}")
//          Toast.makeText(context, "Saved: ${newFile.path}", Toast.LENGTH_SHORT).show()
//        } else throw Exception("Cannot open file")
//      } catch (e: Exception) {
//        Log.e("SaveStatus", e.message, e.fillInStackTrace())
//        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//      }
    }
  }
}