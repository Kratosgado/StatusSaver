package com.kratosgado.statusaver.ui.views

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File

@Composable
fun PermissionScreen(
  onDirectorySelected: (Uri) -> Unit,
  context: Context,
) {
  val launcher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
      uri?.let {
        val flags =
          Intent.FLAG_GRANT_READ_URI_PERMISSION or
              Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        if (checkUriPersisted(context.contentResolver, uri)) {
          context.contentResolver.releasePersistableUriPermission(uri, flags)
        }

        context.contentResolver.takePersistableUriPermission(uri, flags)
        onDirectorySelected(it)
      }
    }
  val initialUri = remember {
    "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses".toUri()
  }
  val whatsappFolder =
    File(
      Environment.getExternalStorageDirectory(),
      "Android/media/com.whatsapp/WhatsApp/Media/.Statuses",
    )
  val whatsappBusinessFolder =
    File(
      Environment.getExternalStorageDirectory(),
      "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses",
    )
  val whatsappUri: Uri? =
    when {
      whatsappFolder.exists() -> DocumentFile.fromFile(whatsappFolder).uri
      whatsappBusinessFolder.exists() -> DocumentFile.fromFile(whatsappBusinessFolder).uri
      else -> null
    }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) { Button(onClick = { launcher.launch(initialUri) }) { Text(text = "Select Status Folder") } }
}

fun checkUriPersisted(
  contentResolver: ContentResolver,
  uri: Uri,
): Boolean = contentResolver.persistedUriPermissions.any { perm -> perm.uri == uri }

fun restoreAccessToDirectory(
  context: Context,
  savedUri: Uri,
) {
  try {
    val contentResolver = context.contentResolver
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    contentResolver.takePersistableUriPermission(savedUri, flags)
  } catch (_: Exception) {
  }
}
