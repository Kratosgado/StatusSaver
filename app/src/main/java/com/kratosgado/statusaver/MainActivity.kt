package com.kratosgado.statusaver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.VideoFrameDecoder
import com.kratosgado.statusaver.ui.MainScreen
import com.kratosgado.statusaver.ui.theme.AppTheme
import com.kratosgado.statusaver.ui.views.StatusPager
import com.kratosgado.statusaver.utils.loadImages
import com.kratosgado.statusaver.utils.repostStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val adManager by lazy { (application as App).adManager }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(adManager)

    setContent {
//      val settingsModel = hiltViewModel<SettingsViewModel>()
//      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
//      val uiState by viewModel.uiState.collectAsState()
      val navController = rememberNavController()
//      val isLoading by settingsModel.isLoading.collectAsState()

      AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val context = LocalContext.current
          val mediaItems = remember { loadImages(context) }
          val imageLoader = remember {
            ImageLoader.Builder(context).components {
              add(VideoFrameDecoder.Factory())
            }.build()
          }
          CompositionLocalProvider(LocalImageLoader provides imageLoader) {
            when {
//              isLoading -> {
//                LaunchedEffect(Unit) {
//                  delay(1000)
//                  viewModel.loadSettings()
//                  Log.d(tag, "Settings loaded")
//                  settingsModel.setLoading(false)
//                }
//                LoadingScreen("Loading ...")
//              }
//
//              uiState.statusDirUri == null -> {
//                PermissionScreen({ uri ->
//                  settingsModel.setStatusLocation(uri)
//                  settingsModel.setLoading(true)
//                }, context = context)
//              }

              else -> {
//                restoreAccessToDirectory(context, uiState.statusDirUri!!)
                NavHost(navController, "main") {
                  composable("main") {
                    MainScreen(
//                      statuses = uiState.statuses.values.toList(),
//                      saved = uiState.saved.values.toList(),
                      statuses = mediaItems,
                      saved = emptyList(),
                      onSaveClick = { status ->
//                        viewModel.saveStatus(status)
//                        Toast.makeText(
//                          context,
//                          "File saved to: ${uiState.savedDirUri}",
//                          Toast.LENGTH_SHORT
//                        )
//                          .show()
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
//                    val statuses = if (isStatus) uiState.statuses else uiState.saved
                    StatusPager(
//                      stats = statuses.values.toList(),
                      stats = mediaItems,
                      startIndex = index,
                      onBack = { navController.popBackStack() },
                      onSaveClick = {},
//                      onSaveClick = { stat -> viewModel.saveStatus(stat) },
                      onShare = { stat -> repostStatus(context, stat) },
                      onRepost = { stat -> repostStatus(context, stat, true) }
                    )
                  }
                }
              }
            }

//            if (uiState.error != null) {
//              ErrorDialog(
//                message = uiState.error!!,
//                onDismiss = { viewModel.clearError() }
//              )
//            }
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