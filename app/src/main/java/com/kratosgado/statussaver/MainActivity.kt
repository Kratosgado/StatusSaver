package com.kratosgado.statussaver

import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.kratosgado.statussaver.ui.MainScreen
import com.kratosgado.statussaver.ui.components.ErrorDialog
import com.kratosgado.statussaver.ui.theme.AppTheme
import com.kratosgado.statussaver.ui.viewmodel.AppViewModel
import com.kratosgado.statussaver.ui.viewmodel.SettingsViewModel
import com.kratosgado.statussaver.ui.views.PermissionScreen
import com.kratosgado.statussaver.ui.views.restoreAccessToDirectory
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  private val adManager by lazy { (application as App).adManager }
  override fun onCreate(savedInstanceState: Bundle?) {
    requestConsent()
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(adManager)
    val savedDir = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
      "StatusSaver"
    ).apply { mkdir() }
    setContent {
      val viewModel: AppViewModel = hiltViewModel<AppViewModel>()
      val settingsModel = hiltViewModel<SettingsViewModel>()
      val uiState by viewModel.uiState.collectAsState()

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

  override fun onResume() {
    super.onResume()
    adManager.loadAd()
  }

  // Add to MainActivity.onCreate()
  private fun requestConsent() {
    val params = ConsentRequestParameters.Builder().build()
    val consentInformation = UserMessagingPlatform.getConsentInformation(this)

    consentInformation.requestConsentInfoUpdate(
      this,
      params,
      {
        if (consentInformation.isConsentFormAvailable) {
          loadConsentForm()
        }
      },
      { Log.e("AdMob", "Consent info update failed: ${it.message}") }
    )
  }

  private fun loadConsentForm() {
    UserMessagingPlatform.loadConsentForm(
      this,
      { form ->
        if (UserMessagingPlatform.getConsentInformation(this).consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
          form.show(this) {
            // Handle dismissal
          }
        }
      },
      { Log.e("AdMob", "Consent form load failed: ${it.message}") }
    )
  }
}