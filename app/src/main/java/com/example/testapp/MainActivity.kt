package com.example.testapp

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.example.testapp.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
  companion object {
    private const val TAG = "MainActivity"
    private const val REQUEST_PERMISSION_CODE = 100
    private const val SAVED_URI = "savedUri"
  }

  private lateinit var sharedPreferences: SharedPreferences

  private val openDocumentTreeLauncher = registerForActivityResult(
    ActivityResultContracts.OpenDocumentTree()
  ){uri: Uri? ->
    if(uri != null){
      val docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
      Log.d(TAG, "Selected directory: ${docUri}")
      listFilesInDirectory(docUri)
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
    Log.d(TAG, "Starting Application: checking permissions")
    openDocumentTreeLauncher.launch(null)

    val storage = Environment.getExternalStorageDirectory()
    val savedDir = "/StatusSaver/"
    if (!storage.resolve(savedDir).exists()) {
      storage.resolve(savedDir).mkdir()
    }


    setContent {
      AppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          CheckPermission(modifier = Modifier.fillMaxSize())
//          MainScreen(
//            appViewModel = AppViewModel(
//              saveDir = storage.absolutePath + savedDir,
//              statusDir = storage.absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/",
//              navController = rememberNavController()
//            )
//          )
        }
      }
    }
  }

  private fun listFilesInDirectory(uri: Uri){
    contentResolver.query(
      uri,
      arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_MIME_TYPE),
      null, null, null
    )?.use { cursor ->
      while (cursor.moveToNext()){
        val name = cursor.getString(0)
        val mimeType = cursor.getString(1)

        Log.d(TAG, "File: $name, type: $mimeType")
      }
    }
  }

  @Composable
  fun CheckPermission(modifier: Modifier, onPermissionGranted: @Composable () -> Unit = {}){
    val context = LocalContext.current
    var isDirectoryPicked by remember { mutableStateOf(false) }
    var pickedFiles by remember { mutableStateOf<List<String>?>(null) }

    // check if URI is already saved in shared preferences
    val savedUriString = sharedPreferences.getString(SAVED_URI, null)
    val savedUri = savedUriString?.let { Uri.parse(it) }

    if(savedUri != null){
      restoreAccessToDirectory(context, savedUri){
        files -> pickedFiles = files
        isDirectoryPicked = true
      }
    }

    val dirPickerLauncher = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.OpenDocumentTree(),
      onResult = {maybeUri ->
        maybeUri?.let { uri ->
          val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

          if(checkUriPersisted(context.contentResolver, uri)){
            context.contentResolver.releasePersistableUriPermission(uri, flags)
          }

          context.contentResolver.takePersistableUriPermission(uri, flags)
          Log.d(TAG, "Directory picked: $uri")

          // save URI to shared preferences
          sharedPreferences.edit().putString(SAVED_URI, uri.toString()).apply()

          val pickedDir = DocumentFile.fromTreeUri(context, uri)
          pickedFiles = pickedDir?.listFiles()?.mapNotNull {
            if(it.isFile) it.name else null
          }

          pickedFiles?.forEach { fileName -> Log.d(TAG, "File: $fileName") }

          isDirectoryPicked = true
        }
  })

    Column(modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
      Button(onClick = { dirPickerLauncher.launch(Uri.EMPTY) }) {
        Text(text = "Open Directory Picker")
      }
      Spacer(modifier = Modifier.height(30.dp))

      if (isDirectoryPicked) {
        Text("Directory picked successfully! Files:")
        pickedFiles?.let { uris ->
          LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(16.dp)
          ) {
            items(uris.size) { index ->
              val imageUri = uris[index]
              Image(
                painter = rememberImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                  .size(100.dp)
                  .padding(4.dp)
              )
            }
          }
        }
        onPermissionGranted()
      } else {
        Text(text = "Waiting for you to choose a directory")
      }
    }
  }

  fun checkUriPersisted(contentResolver: ContentResolver, uri: Uri): Boolean {
    return contentResolver.persistedUriPermissions.any{
      perm -> perm.uri == uri
    }
  }

  private fun restoreAccessToDirectory(context: Context, savedUri: Uri, onFilesRetrieved: (List<String>) -> Unit){
    try {
      val contentResolver = context.contentResolver
      val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
      contentResolver.takePersistableUriPermission(savedUri, flags)

      val documentFile =  DocumentFile.fromTreeUri(context, savedUri)
      val files = documentFile?.listFiles()?.mapNotNull {
        it.name
      } ?: emptyList()

      onFilesRetrieved(files)
    } catch (e: Exception){
      Log.e(TAG, "Error restoring access to directory: ${e.message}")
    }
  }

  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_PERMISSION_CODE) {
      if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
        //
      } else {
        Log.d("Permissions", "${grantResults.find { it != PackageManager.PERMISSION_GRANTED }}")
      }
    }
  }

  private fun checkPermissions() {
    if (!hasWritePermissions()) {
      requestPermissions()
    }
  }

  private fun hasWritePermissions(): Boolean {
    return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestPermissions() {
    requestPermissions(
      arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
      ),
      REQUEST_PERMISSION_CODE,
    )
  }
}
