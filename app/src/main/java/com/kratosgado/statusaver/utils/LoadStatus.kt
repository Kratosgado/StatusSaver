package com.kratosgado.statusaver.utils

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.kratosgado.statusaver.domain.Status
import com.kratosgado.statusaver.domain.StatusType

fun loadImages(context: Context): List<Status> {
  Log.d("loadImages", "Loading images")
  val imageUris = mutableListOf<Status>()
  val possiblePaths = listOf(
    "WhatsApp/Media/.Statuses/",
    "Android/media/com.whatsapp/WhatsApp/Media/.Statuses/",
    "WhatsApp Business/Media/.Statuses/"
  )
  val projection = arrayOf(
    MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED,
    MediaStore.Images.Media.DISPLAY_NAME
  )
  val videoProjection = arrayOf(
    MediaStore.Video.Media._ID,
    MediaStore.Video.Media.DATE_ADDED,
    MediaStore.Video.Media.DISPLAY_NAME
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
  for (path in possiblePaths) {
    val statusPath = if (isVersionAbove10) {
      path
    } else {
      val externalStorage = Environment.getExternalStorageDirectory().path
      "$externalStorage/$path"
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
        val displayName =
          it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        val pathColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
        } else {
          cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        }
        imageUris.add(
          Status(
            uri = uri,
            name = displayName,
            isSaved = false,
            type = StatusType.Image,
            date = dateAdded
          )
        )
        Log.d("VideoQuery", "Found video: $displayName, Path: $pathColumn, URI: $uri")
      }
    }
// Query Videos
    videoCursor?.use {
      while (it.moveToNext()) {
        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
        val dateAdded = it.getLong(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
        val displayName =
          it.getString(it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
        val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
        imageUris.add(Status(uri, displayName, false, StatusType.Video, dateAdded))
      }
    }

    Log.d(
      "loadImages",
      "Path: $statusPath, Selection: $videoSelection, Args: ${selectionArgs.joinToString()}"
    )
  }

  Log.d("loadImages", "Loaded ${imageUris.size} images")
  return imageUris.sortedByDescending { it.date }
}