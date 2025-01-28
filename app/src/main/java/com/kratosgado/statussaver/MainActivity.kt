package com.kratosgado.statussaver

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.MobileAds
import com.kratosgado.statussaver.ui.MainScreen
import com.kratosgado.statussaver.ui.components.ErrorDialog
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
import com.kratosgado.statussaver.ui.views.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val backgroundScope = CoroutineScope(Dispatchers.IO)
    backgroundScope.launch {
      MobileAds.initialize(this@MainActivity) {}
    }
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
          val context = LocalContext.current
          if (uiState.statusDirUri == null) {
            PermissionScreen { uri ->
              viewModel.setSaveDir(uri, savedDir)
              viewModel.loadStatuses()
            }
          } else {
            MainScreen(
              statuses = uiState.statuses.values.toList(),
              saved = uiState.saved.values.toList(),
              onSaveClick = { status ->
                viewModel.saveStatus(status)
                Toast.makeText(context, "File saved to: ${uiState.savedDirUri}", Toast.LENGTH_SHORT)
                  .show()
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