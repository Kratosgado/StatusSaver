package com.kratosgado.statussaver

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.kratosgado.statussaver.ui.MainScreen
import com.kratosgado.statussaver.ui.components.ErrorDialog
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
import com.kratosgado.statussaver.ui.views.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val savedDir =
        File(
          Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
          "StatusSaver"
        )
      if (!savedDir.exists()) {
        savedDir.mkdir()
      }
      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
      val uiState by viewModel.uiState.collectAsState()

      AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          if (uiState.statusDirUri == null) {
            PermissionScreen { uri ->
              viewModel.setSaveDir(uri, savedDir)
              viewModel.loadStatuses()
            }
          } else {
            MainScreen(
              statuses = uiState.statuses,
              saved = uiState.saved,
              onSaveClick = { status ->
                viewModel.saveStatus(status)
              },
              onShareClick = { /* Handle share */ },
              onSendClick = { /* Handle send */ }
            )
          }

          if (uiState.error != null) {
            ErrorDialog(
              message = uiState.error!!,
              onDismiss = { viewModel.clearError() }
            )
          }
        }
      }
    }
  }
}