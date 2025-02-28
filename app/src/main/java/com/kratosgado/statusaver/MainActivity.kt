package com.kratosgado.statusaver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
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
import com.kratosgado.statusaver.ui.MainScreen
import com.kratosgado.statusaver.ui.components.ErrorDialog
import com.kratosgado.statusaver.ui.theme.AppTheme
import com.kratosgado.statusaver.ui.viewmodel.AppViewModel
import com.kratosgado.statusaver.ui.viewmodel.SettingsViewModel
import com.kratosgado.statusaver.ui.views.LoadingScreen
import com.kratosgado.statusaver.ui.views.PermissionScreen
import com.kratosgado.statusaver.ui.views.StatusPager
import com.kratosgado.statusaver.ui.views.restoreAccessToDirectory
import com.kratosgado.statusaver.utils.repostStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val adManager by lazy { (application as App).adManager }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(adManager)
    setContent {
      val settingsModel = hiltViewModel<SettingsViewModel>()
      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
      val uiState by viewModel.uiState.collectAsState()
      val navController = rememberNavController()
      val isLoading by settingsModel.isLoading.collectAsState()

      AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val context = LocalContext.current
          when {
            isLoading -> {
              LaunchedEffect(Unit) {
                delay(1000)
                viewModel.loadSettings()
                Log.d(tag, "Settings loaded")
                settingsModel.setLoading(false)
              }
              LoadingScreen("Loading ...")
            }

            uiState.statusDirUri == null -> {
              PermissionScreen({ uri ->
                settingsModel.setStatusLocation(uri)
                settingsModel.setLoading(true)
              }, context = context)
            }

            else -> {
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
                    onShare = { stat -> repostStatus(context, stat) },
                    onRepost = { stat -> repostStatus(context, stat, true) }
                  )
                }
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
    Log.d(tag, "App resumed")
    adManager.loadAd()
    super.onResume()
  }

  override fun onStart() {
    adManager.onMoveToForeground()
    super.onStart()
  }

  override fun onPause() {
    Log.d(tag, "App paused")
    super.onPause()
  }


  private fun shareApp() {
    val shareText = "Check out this awesome status saver app!\n" +
        "https://play.google.com/store/apps/details?id=$packageName"
    Intent.createChooser(
      Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
      },
      "Share to friends"
    ).also { this.startActivity(it) }
  }

  companion object {
    const val tag = "MainActivity"
  }
}