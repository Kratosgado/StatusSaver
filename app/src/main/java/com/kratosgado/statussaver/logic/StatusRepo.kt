package com.kratosgado.statussaver.logic

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statussaver.utils.SettingsManager
import com.kratosgado.statussaver.utils.Status
import java.io.File
import java.io.FileOutputStream

class StatusRepo(private val context: Context) {
  private val prefs = SettingsManager.getInstance(context)

  suspend fun loadStatuses(): Pair<List<Status>, List<Status>> {
    val statusDirUri = prefs.getValue(SettingsManager.STATUS_DIR, "")

    val statusDir = DocumentFile.fromTreeUri(context, Uri.parse(statusDirUri))
    val images = mutableListOf<Status>()
    val videos = mutableListOf<Status>()

    statusDir?.listFiles()?.forEach {
      when {
        it.name!!.endsWith(".jpg") || it.name!!.endsWith(".jpeg") -> images.add(
          Status(uri = it.uri, isSaved = false)
        )

        it.name!!.endsWith(".mp4") -> videos.add(
          Status(uri = it.uri, isSaved = false)
        )
      }
    }
    return images to videos
  }

  //  suspend fun loadSavedStats(): List<Status> {}
  suspend fun saveStatus(uri: Uri): Boolean {
    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(uri)

    if (inputStream != null) {
      val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        "StatusSaver"
      )
      if (!directory.exists()) {
        directory.mkdirs()
      }
      val file = DocumentFile.fromSingleUri(context, uri)
      val newFile = File(directory, file?.name ?: "default.jpg")
      if (newFile.exists()) throw Exception("File has been already saved")
      FileOutputStream(newFile).use {
        inputStream.copyTo(it)
        inputStream.close()
      }
      val index = images.indexOfFirst { it.first == uri }
      images[index] = uri to true
      Log.d("StatusItem", "File saved as ${newFile.path}")
      Toast.makeText(context, "Saved: ${newFile.path}", Toast.LENGTH_SHORT).show()
    }
  }
}