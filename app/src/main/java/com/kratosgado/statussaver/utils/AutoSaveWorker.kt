package com.kratosgado.statussaver.utils

import android.content.Context
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AutoSaveWorker(private val context: Context, params: WorkerParameters) :
  CoroutineWorker(context, params) {
  // Use Dispatchers.IO for background operations
  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {
      // Add your auto-save logic here (e.g., check WhatsApp folder)
      TODO("auto save image")
//      Result.success()
    } catch (e: Exception) {
      Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
      Result.failure()
    }
  }
}

fun scheduleAutoSave(context: Context) {
  val constraints = Constraints.Builder()
    .setRequiresStorageNotLow(true) // Only run when storage is sufficient
    .build()

  val workRequest = PeriodicWorkRequestBuilder<AutoSaveWorker>(
    15, // Repeat interval
    TimeUnit.MINUTES
  )
    .setConstraints(constraints)
    .build()

  WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    SettingsManager.AUTO_SAVE_WORKER,
    ExistingPeriodicWorkPolicy.REPLACE, // Replace existing work
    workRequest
  )
}