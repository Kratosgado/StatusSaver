package com.kratosgado.statussaver.ui.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PermissionScreen(
  onDirectorySelected: (Uri) -> Unit
) {
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocumentTree()
  ) { uri ->
    uri?.let { onDirectorySelected(it) }
  }

  Column(Modifier.fillMaxSize()) {
    Text("Please select the WhatsApp status folder")
    Button(onClick = { launcher.launch(null) }) {
      Text("Select Folder")
    }
  }
}