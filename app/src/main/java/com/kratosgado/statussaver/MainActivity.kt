package com.kratosgado.statussaver

import android.content.Intent
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kratosgado.statussaver.ui.MainScreen
import com.kratosgado.statussaver.ui.components.ErrorDialog
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
import com.kratosgado.statussaver.ui.viewmodel.SettingsViewModel
import com.kratosgado.statussaver.ui.views.PermissionScreen
import com.kratosgado.statussaver.ui.views.StatusPager
import com.kratosgado.statussaver.ui.views.restoreAccessToDirectory
import com.kratosgado.statussaver.utils.repostStatus
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val adManager by lazy { (application as App).adManager }
  override fun onCreate(savedInstanceState: Bundle?) {
//    requestConsent()
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(adManager)
    val savedDir = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      "StatusSaver"
    ).apply { mkdir() }
    setContent {
      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
      val settingsModel = hiltViewModel<SettingsViewModel>()
      val uiState by viewModel.uiState.collectAsState()
      val navController = rememberNavController()

      AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val context = LocalContext.current
          if (uiState.statusDirUri == null) {
            PermissionScreen({ uri ->
              settingsModel.setStatusLocation(uri)
              viewModel.setSaveDir(uri, savedDir)
              viewModel.loadStatuses()
            }, context = context)
          } else {
            restoreAccessToDirectory(context, uiState.statusDirUri!!)
            NavHost(navController, "main") {
              composable("main") {
                MainScreen(
                  statuses = uiState.statuses.values.toList(),
                  saved = uiState.saved.values.toList(),
                  onSaveClick = { status ->
                    viewModel.saveStatus(status)
                    Toast.makeText(
                      context,
                      "File saved to: ${uiState.savedDirUri}",
                      Toast.LENGTH_SHORT
                    )
                      .show()
                  },
                  onShareClick = { shareApp() },
                  onItemClick = {
                    navController.navigate("status/${it.first}/${it.second}")
                  },
                )
              }
              composable(
                "status/{index}/{isStatus}",
                arguments = listOf(
                  navArgument("index") { type = NavType.IntType },
                  navArgument("isStatus") { type = NavType.BoolType })
              ) {
                val index = it.arguments?.getInt("index") ?: 0
                val isStatus = it.arguments?.getBoolean("isStatus") ?: true
                val statuses = if (isStatus) uiState.statuses else uiState.saved
                StatusPager(
                  stats = statuses.values.toList(),
                  startIndex = index,
                  onBack = { navController.popBackStack() },
                  onSaveClick = { stat -> viewModel.saveStatus(stat) },
                  onShare = { stat -> repostStatus(context, uiState.savedDirUri!!, stat) },
                  onRepost = { stat -> repostStatus(context, uiState.savedDirUri!!, stat, true) }
                )
              }
            }
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

  override fun onResume() {
    super.onResume()
    adManager.loadAd()
  }

  override fun onStart() {
    super.onStart()
    adManager.onMoveToForeground()
  }


  private fun shareApp() {
    val shareText = "Check out this awesome status saver app!\n" +
        "https://play.google.com/store/apps/details?id=$packageName"
    val shareIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, shareText)
      type = "text/plain"
    }
    startActivity(Intent.createChooser(shareIntent, "Share to friends"))
  }

}