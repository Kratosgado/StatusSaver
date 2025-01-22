package com.kratosgado.statussaver.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.kratosgado.statussaver.ui.Screens
import com.kratosgado.statussaver.utils.isExternalStorageWritable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream

class AppViewModel(
  val navController: NavHostController,
  val context: Context,
  statusDir: Uri,
  saveDir: File,
) : ViewModel() {

  // ui state
  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  val images: MutableList<Pair<Uri, Boolean>> = mutableListOf()
  val videos: MutableList<Pair<Uri, Boolean>> = mutableListOf()
  val saved: MutableList<Pair<Uri, Boolean>> = mutableListOf()

  init {
    val whatsappStatusDir = DocumentFile.fromTreeUri(context, statusDir)

    val savedFilesNames = saveDir
      .listFiles()?.map {
        saved.add(it.toUri() to true)
        Log.d("ImageView", it.name)
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

  fun navigateBack() {
    navController.popBackStack()
    _uiState.update {
      it.copy(
        selectedScreen = Screens.valueOf(
          navController.currentDestination?.route ?: Screens.Images.name
        )
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
    try {
      Log.d("Saving", "Saving")
      val resolver = context.contentResolver
      val inputStream = resolver.openInputStream(uri)

      if (inputStream != null) {
        if (isExternalStorageWritable()) Log.d("Saving", "Writable")
        val directory = File(
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
          "StatusSaver"
        )
        if (!directory.exists()) {
          Log.d("Saving", "creating directory")
          directory.mkdirs()
        }
        val file = DocumentFile.fromSingleUri(context, uri)
        val newFile = File(directory, file?.name ?: "default.jpg")
        if (newFile.exists()) throw Exception("File has been already saved")
        FileOutputStream(newFile).use {
          inputStream.copyTo(it)
          inputStream.close()
        }
        val index = images.indexOfFirst { it.first == uri }
        images[index] = uri to true
        Log.d("StatusItem", "File saved as ${newFile.path}")
        Toast.makeText(context, "Saved: ${newFile.path}", Toast.LENGTH_SHORT).show()
        return
      }
      throw Exception("Cannot open file")
    } catch (e: Exception) {
      Log.e("SaveStatus", e.message, e.fillInStackTrace())
      Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//    TODO("Not yet implemented: Saving status error")
    }
  }
}