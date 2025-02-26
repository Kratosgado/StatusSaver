package com.kratosgado.statusaver.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.kratosgado.statusaver.domain.Status
import com.kratosgado.statusaver.domain.StatusType
import java.io.File
import java.io.FileOutputStream

// Add this to your MainActivity or ViewModel
fun repostStatus(context: Context, status: Status, repost: Boolean = false) {
  try {
    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(status.uri) ?: run {
      Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show()
      return
    }
    val mimeType = resolver.getType(status.uri) ?: when (status.type) {
      StatusType.Image -> "image/jpg"
      StatusType.Video -> "video/mp4"
    }

    val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

    val tempFile = File.createTempFile("share_", ".$extension", context.cacheDir)
    FileOutputStream(tempFile).use {
      inputStream.copyTo(it)
    }

    // Create URI using FileProvider
    val contentUri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      tempFile
    )

    Log.d("Reposting", "reposting $contentUri")
    if (repost) {
      repost(context, contentUri, mimeType)
    } else {
      shareStatus(context, contentUri, mimeType)
    }

    Handler(Looper.getMainLooper()).postDelayed({
      if (tempFile.exists()) tempFile.delete()
    }, 5 * 50 * 1000)
  } catch (e: Exception) {
    Log.d("Reposting", e.message ?: e.toString())
    Toast.makeText(context, "Error sharing to WhatsApp", Toast.LENGTH_SHORT).show()
  }
}

private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
  return try {
    packageManager.getPackageInfo(packageName, 0)
    true
  } catch (e: PackageManager.NameNotFoundException) {
    false
  }
}

private fun repost(context: Context, uri: Uri, mimeType: String) {
  val packageManager = context.packageManager
  val whatsappPackage = when {
    isPackageInstalled("com.whatsapp", packageManager) -> "com.whatsapp"
    isPackageInstalled("com.whatsapp.w4b", packageManager) -> "com.whatsapp.w4b"
    else -> null
  }

  if (whatsappPackage == null) {
    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    return
  }
  // Create intent
  Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_STREAM, uri)
    type = mimeType
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    setPackage(whatsappPackage)
    context.startActivity(this)
  }
}

private fun shareStatus(context: Context, uri: Uri, mimeType: String) {
  Intent.createChooser(
    Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_STREAM, uri)
      type = mimeType
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    },
    "Share via"
  ).also { context.startActivity(it) }
}