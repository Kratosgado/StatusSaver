package com.kratosgado.statussaver.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statussaver.domain.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class StatusRepository(private val context: Context) {

  suspend fun loadStatuses(statusDirUri: Uri): List<Status> = withContext(Dispatchers.IO) {
    val statusDir = DocumentFile.fromTreeUri(context, statusDirUri)
    val statuses = mutableListOf<Status>()

    statusDir?.listFiles()?.forEach { file ->
      when {
        isImage(file) -> statuses.add(
          Status.Image(
            uri = file.uri,
            name = file.name ?: "Untitled"
          )
        )

        isVideo(file) -> statuses.add(
          Status.Video(
            uri = file.uri,
            name = file.name ?: "Untitled"
          )
        )
      }
    }
    return@withContext statuses
  }

  suspend fun saveStatus(sourceUri: Uri, saveDirUri: Uri): Boolean = withContext(Dispatchers.IO) {
    try {
      val saveDir = DocumentFile.fromTreeUri(context, saveDirUri)
      val sourceFile = DocumentFile.fromSingleUri(context, sourceUri)
      val inputStream = context.contentResolver.openInputStream(sourceUri)

      if (saveDir == null || sourceFile == null || inputStream == null) {
        throw IOException("Invalid file or directory")
      }

      val existingFile = saveDir.findFile(sourceFile.name ?: "")
      if (existingFile != null) throw IOException("File already exists")

      val newFile = saveDir.createFile(
        sourceFile.type ?: "image/jpeg",
        sourceFile.name ?: "status_${System.currentTimeMillis()}"
      )

      context.contentResolver.openOutputStream(newFile?.uri ?: throw IOException())?.use { output ->
        inputStream.copyTo(output)
      }

      true
    } catch (e: IOException) {
      false
    }
  }

  private fun isImage(file: DocumentFile): Boolean {
    return file.type?.startsWith("image/") == true || file.name?.endsWith(".jpg") == true
  }

  private fun isVideo(file: DocumentFile): Boolean {
    return file.type?.startsWith("video/") == true || file.name?.endsWith(".mp4") == true
  }
}