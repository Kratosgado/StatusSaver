package com.kratosgado.statussaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kratosgado.statussaver.ui.components.ErrorDialog
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
import com.kratosgado.statussaver.ui.views.PermissionScreen
import com.kratosgado.statussaver.ui.views.StatusGrid
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
//      val navController = rememberNavController()
      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
      val uiState by viewModel.uiState.collectAsState()

      AppTheme {
        if (uiState.saveDirUri == null) {
          PermissionScreen { uri ->
            viewModel.setSaveDir(uri)
            viewModel.loadStatuses(uri)
          }
        } else {
          StatusGrid(
            statuses = uiState.statuses,
            onSaveClick = { status ->
              viewModel.saveStatus(status, uiState.saveDirUri!!)
            },
            onItemClick = { /* Handle navigation */ }
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