package com.kratosgado.statussaver.data

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statussaver.domain.Status
import com.kratosgado.statussaver.domain.StatusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StatusRepository(private val context: Context) {

  suspend fun loadStatuses(
    statusDirUri: Uri,
    saveDirUri: File
  ): Pair<Map<String, Status>, Map<String, Status>> =
    withContext(Dispatchers.IO) {
      val statusDir = DocumentFile.fromTreeUri(context, statusDirUri)
      val savedDir = DocumentFile.fromFile(saveDirUri)
      val statuses = mutableMapOf<String, Status>()
      val saved = mutableMapOf<String, Status>()

      savedDir.listFiles().forEach { file ->
        val type = when {
          isImage(file) -> StatusType.Image
          isVideo(file) -> StatusType.Video
          else -> return@forEach
        }
        saved[file.name!!] = Status(
          uri = file.uri,
          name = file.name ?: "Untitled",
          isSaved = true,
          type = type
        )
      }
      statusDir?.listFiles()?.forEach { file ->
        val type = when {
          isImage(file) -> StatusType.Image
          isVideo(file) -> StatusType.Video
          else -> return@forEach
        }
        statuses[file.name!!] = Status(
          uri = file.uri,
          name = file.name ?: "Untitled",
          isSaved = saved.containsKey(file.name),
          type = type
        )
      }
      return@withContext Pair(statuses, saved)
    }

  suspend fun saveStatus(uri: Uri, savedDir: File): Boolean = withContext(Dispatchers.IO) {
    try {
      val resolver = context.contentResolver
      val inputStream = resolver.openInputStream(uri)
      val file = DocumentFile.fromSingleUri(context, uri)
      val newFile = File(savedDir, file?.name ?: "default.jpg")

      FileOutputStream(newFile).use {
        inputStream?.copyTo(it)
        inputStream?.close()
      }
      // Scan the new file so it appears in gallery
      MediaScannerConnection.scanFile(
        context,
        arrayOf(newFile.absolutePath),
        null
      ) { path, uri ->
        println("Scanned $path: $uri")
      }
      true
    } catch (e: IOException) {
      false
    }
  }

  suspend fun deleteStat(uri: Uri, savedDir: File): Boolean = withContext(Dispatchers.IO) {
    try {
      val file = DocumentFile.fromSingleUri(context, uri)
      val fileName = file?.name

      val publicFile = File(savedDir, fileName ?: return@withContext false)
      if (publicFile.exists()) {
        publicFile.delete()
        // Notify media scanner about deletion
        MediaScannerConnection.scanFile(
          context,
          arrayOf(publicFile.absolutePath),
          null,
          null
        )
      }
      true
    } catch (e: IOException) {
      e.printStackTrace()
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