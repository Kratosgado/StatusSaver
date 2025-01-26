package com.kratosgado.statussaver.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kratosgado.statussaver.data.StatusRepository
import com.kratosgado.statussaver.domain.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
  val handle: SavedStateHandle,
  private val repository: StatusRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  fun loadStatuses(statusDirUri: Uri, saveDirUri: Uri) {
    _uiState.value = _uiState.value.copy(isLoading = true)
    viewModelScope.launch {
      try {
        val statuses = repository.loadStatuses(statusDirUri, saveDirUri)
        _uiState.value = _uiState.value.copy(
          statuses = statuses,
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

  fun saveStatus(status: Status, saveDirUri: Uri) {
    viewModelScope.launch {
      val uri = when (status) {
        is Status.Image -> status.uri
        is Status.Video -> status.uri
      }
      val success = repository.saveStatus(uri, saveDirUri)
      if (!success) {
        _uiState.value = _uiState.value.copy(
          error = "Failed to save status"
        )
      }
    }
  }

  fun setSaveDir(uri: Uri) {
    _uiState.value = _uiState.value.copy(statusDirUri = uri)
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }
}