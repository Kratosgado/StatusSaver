package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kratosgado.statussaver.data.SettingsManager
import com.kratosgado.statussaver.data.StatusRepository
import com.kratosgado.statussaver.domain.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
  val handle: SavedStateHandle,
  private val repository: StatusRepository,
  private val settingsManager: SettingsManager
) : ViewModel() {

  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  init {
    loadSettings()
  }

  private fun loadSettings() {
    viewModelScope.launch {
      settingsManager.statusLocation.collect {
        it?.let {
          val savedDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "StatusSaver"
          )
          setSaveDir(it, savedDir)
          loadStatuses()
        }
      }
    }
  }

  fun loadStatuses() {
    _uiState.value = _uiState.value.copy(isLoading = true)
    viewModelScope.launch {
      try {
        val (statuses, saved) = repository.loadStatuses(
          _uiState.value.statusDirUri!!,
          _uiState.value.savedDirUri!!
        )
        _uiState.value = _uiState.value.copy(
          statuses = statuses,
          saved = saved,
          isLoading = false,
          error = null
        )
      } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
          error = "Failed to load statuses: ${e.message}",
          isLoading = false
        )
      }
    }
  }

  fun saveStatus(status: Status) {
    viewModelScope.launch {
      val stats = _uiState.value.statuses.toMutableMap()
      val saved = _uiState.value.saved.toMutableMap()
      var success = false
      if (status.isSaved) {
        success = !repository.deleteStat(status.uri, _uiState.value.savedDirUri!!)
      } else {
        success = repository.saveStatus(status.uri, _uiState.value.savedDirUri!!)
        if (!success) {
          _uiState.value = _uiState.value.copy(
            error = "Failed to save status"
          )
          return@launch
        }
      }
      stats[status.name] = status.copy(isSaved = success)
      saved[status.name] = status.copy(isSaved = success)
      _uiState.value = _uiState.value.copy(
        statuses = stats,
        saved = saved
      )
    }
  }

  fun setSaveDir(uri: Uri, saveDirUri: File) {
    _uiState.value = _uiState.value.copy(statusDirUri = uri, savedDirUri = saveDirUri)
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }
}