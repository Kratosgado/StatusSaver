package com.kratosgado.statusaver.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kratosgado.statusaver.data.SettingsManager
import com.kratosgado.statusaver.data.StatusRepository
import com.kratosgado.statusaver.domain.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
  private val repository: StatusRepository,
  private val settingsManager: SettingsManager
) : ViewModel() {

  private val _uiState = MutableStateFlow(AppUiState())
  val uiState = _uiState.asStateFlow()

  suspend fun loadSettings() {
    val stat = settingsManager.statusLocation.first()
    val saved = settingsManager.saveLocation.first()
    _uiState.value =
      _uiState.value.copy(statusDirUri = stat, savedDirUri = saved!!.toFile())
    if (stat != null) loadStatuses()
    Log.d(tag, "initialize settings")
  }

  suspend fun loadStatuses() {
    try {
      repository.loadStatuses(
        _uiState.value.statusDirUri!!,
        _uiState.value.savedDirUri!!
      ).let { (statuses, saved) ->
        _uiState.value = _uiState.value.copy(
          statuses = statuses,
          saved = saved,
          error = null
        )
      }
      Log.d(tag, "Status loaded")
    } catch (e: Exception) {
      _uiState.value = _uiState.value.copy(
        error = "Failed to load statuses: ${e.message}",
      )
    }
  }

  fun saveStatus(status: Status) {
    var message = ""
    viewModelScope.launch {
      val stats = _uiState.value.statuses.toMutableMap()
      val saved = _uiState.value.saved.toMutableMap()
      val success: Boolean
      if (status.isSaved) {
        success = !repository.deleteStat(status.uri, _uiState.value.savedDirUri!!)
        saved.remove(status.name)
        "File deleted successfully".also { message = it }
      } else {
        success = repository.saveStatus(status.uri, _uiState.value.savedDirUri!!)
        saved[status.name] = status.copy(isSaved = success)
        if (!success) {
          _uiState.value = _uiState.value.copy(
            error = "Failed to save status"
          )
          return@launch
        }
        "File saved to ${_uiState.value.savedDirUri}".also { message = it }
      }
      if (stats.containsKey(status.name)) {
        stats[status.name] = status.copy(isSaved = success)
      }
      _uiState.value = _uiState.value.copy(
        statuses = stats,
        saved = saved
      )
    }
  }

  fun setStatusDir(uri: Uri) {
    _uiState.value = _uiState.value.copy(statusDirUri = uri)
  }

  fun clearError() {
    _uiState.value = _uiState.value.copy(error = null)
  }

  companion object {
    const val tag = "AppMiewModel"
  }
}