package com.example.statussaver.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.statussaver.ui.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class AppViewModel(
  val navController: NavHostController,
  context: Context,
  statusDir: Uri,
  saveDir: String
): ViewModel() {
  companion object {
    const val tag = "AppViewModel";
  }
  // ui state
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  val images: MutableList<Pair<Uri, Boolean>> = mutableListOf()
  val videos: MutableList<Pair<Uri, Boolean>> = mutableListOf()
  val saved: MutableList<Pair<Uri, Boolean>> = mutableListOf()

  init {
    val whatsappStatusDir = DocumentFile.fromTreeUri(context, statusDir)
    val savedFiles = File(saveDir)

    val savedFilesNames = savedFiles
      .listFiles()?.map {
        saved.add(it.toUri() to true )
        it.name
      }
    whatsappStatusDir?.listFiles()?.forEach {
      when {
        it.name!!.endsWith(".jpg") || it.name!!.endsWith(".jpeg") -> {
          images.add(it.uri to (savedFilesNames?.contains(it.name!!) ?: false))
        }

        it.name!!.endsWith(".mp4") -> {
          videos.add(it.uri to (savedFilesNames?.contains(it.name!!) ?: false))
        }
      }
    }
  }

  // handle events
  fun switchScreen(screen: Screens) {
    navController.navigate(screen.name)
    _uiState.update {
      it.copy(selectedScreen = screen)
    }
  }
  private fun debug(v: String){
    Log.d(tag, v)
  }

  fun navigateBack(){
    navController.popBackStack()
    _uiState.update {
      it.copy(selectedScreen = Screens.valueOf(navController.currentDestination?.route ?: Screens.Images.name))
    }
  }

  fun viewStatus(index: Int){
    _uiState.update {
      it.copy(selectedScreen = Screens.StatusView, canNavigateBack = true)
    }
    debug("Navigating to $index")
    navController.navigate("${Screens.StatusView.name}/$index")
//    navController.navigate("${Screens.StatusView.name}")
  }

  fun previousScreen(): Screens {
    return Screens.valueOf(navController.previousBackStackEntry?.destination?.route ?: Screens.Images.name)
  }
}