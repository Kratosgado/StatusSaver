package com.kratosgado.statussaver.logic

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statussaver.utils.SettingsManager
import com.kratosgado.statussaver.utils.Status

class StatusRepo(private val context: Context) {
  private val prefs = SettingsManager.getInstance(context)

  suspend fun loadStatuses(): List<Status> {
    val statusDirUri = prefs.getValue(SettingsManager.STATUS_DIR, "")

    val statusDir = DocumentFile.fromTreeUri(context, Uri.parse(statusDirUri))
    val statuses = mutableListOf<Status>()

    statusDir?.listFiles()?.forEach {
      when {
        it.name!!.endsWith(".jpg") || it.name!!.endsWith(".jpeg") -> statuses.add(
          Status.Image(
            uri = it.uri,
            isSaved = false
          )
        )

        it.name!!.endsWith(".mp4") -> statuses.add(
          Status.Video(
            uri = it.uri,
            isSaved = false
          )
        )
      }
    }
    return statuses
  }

//  suspend fun loadSavedStats(): List<Status> {}
//  suspend fun saveStatus(uri: Uri): Boolean {}
}