package com.kratosgado.statusaver.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.kratosgado.statusaver.domain.Status
import com.kratosgado.statusaver.domain.StatusType
import java.io.File
import java.io.FileOutputStream

// Add this to your MainActivity or ViewModel
fun repostStatus(context: Context, savedDir: File, status: Status, repost: Boolean = false) {
  try {


    val resolver = context.contentResolver
    val inputStream = resolver.openInputStream(status.uri)
    val file = DocumentFile.fromSingleUri(context, status.uri)
    val newFile = File(savedDir, file?.name ?: "default.jpg")

    val delete = !newFile.exists()
    if (delete)
      FileOutputStream(newFile).use {
        inputStream?.copyTo(it)
        inputStream?.close()
      }

    // Create URI using FileProvider
    val contentUri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      newFile
    )
    if (repost) {
      repost(context, contentUri, status.type)
    } else {
      shareStatus(context, contentUri, status.type)
    }

    if (delete) newFile.delete()
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

private fun repost(context: Context, uri: Uri, t: StatusType) {
  // WhatsApp package names
  val whatsappPackage = "com.whatsapp"
  val whatsappBusinessPackage = "com.whatsapp.w4b"
// Check if WhatsApp is installed
  val packageManager = context.packageManager
  val whatsappInstalled = isPackageInstalled(whatsappPackage, packageManager)
  val whatsappBusinessInstalled = isPackageInstalled(whatsappBusinessPackage, packageManager)

  if (!whatsappInstalled && !whatsappBusinessInstalled) {
    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    return
  }
  // Create intent
  val intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_STREAM, uri)
    type = when (t) {
      StatusType.Image -> "image/*"
      StatusType.Video -> "video/*"
    }
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Try regular WhatsApp first, then WhatsApp Business
    when {
      whatsappInstalled -> setPackage(whatsappPackage)
      whatsappBusinessInstalled -> setPackage(whatsappBusinessPackage)
    }
  }

  context.startActivity(intent)
}

private fun shareStatus(context: Context, uri: Uri, t: StatusType) {

  val shareIntent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_STREAM, uri)
    type = when (t) {
      StatusType.Video -> "video/*"
      StatusType.Image -> "image/*"
    }
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
  }
  context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}