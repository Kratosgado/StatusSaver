package com.kratosgado.statusaver.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statusaver.domain.Status
import com.kratosgado.statusaver.domain.StatusType
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

  fun loadImages(): List<Status> {
    val imageUris = mutableListOf<Status>()
    val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
    val videoProjection = arrayOf(
      MediaStore.Video.Media._ID,
      MediaStore.Video.Media.DATE_ADDED
    )
    val isVersionAbove10 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val selection = if (isVersionAbove10) {
      "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    } else {
      "${MediaStore.Images.Media.DATA} LIKE ?"
    }
    val videoSelection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
    } else {
      "${MediaStore.Video.Media.DATA} LIKE ?"
    }
    val statusPath = if (isVersionAbove10) {
      "WhatsApp/Media/.Statuses"
    } else {
      val externalStorage = Environment.getExternalStorageDirectory().path
      "$externalStorage/WhatsApp/Media/.Statuses"
    }
    val selectionArgs = arrayOf("$statusPath%")
    val cursor = context.contentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      projection, selection, selectionArgs, null
    )
    val videoCursor =
      context.contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        videoProjection,
        videoSelection,
        selectionArgs,
        null
      )
    cursor?.use {
      while (it.moveToNext()) {
        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
        val dateAdded = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED))
        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageUris.add(
          Status(
            uri = uri,
            name = "$id",
            isSaved = false,
            type = StatusType.Image,
            date = dateAdded
          )
        )
      }
    }
// Query Videos
    videoCursor?.use {
      while (it.moveToNext()) {
        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
        val dateAdded = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
        val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
        imageUris.add(Status(uri, "$id", false, StatusType.Video, dateAdded))
      }
    }
    return imageUris.sortedByDescending { it.date }
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