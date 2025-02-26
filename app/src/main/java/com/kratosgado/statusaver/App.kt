package com.kratosgado.statusaver
// File: App.kt
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.kratosgado.statusaver.data.OpenAdManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {
  companion object {
    private const val tag = "MainApp"
  }

  val adManager by lazy { OpenAdManager(this) }

  override fun onCreate() {
    super.onCreate()
    registerActivityLifecycleCallbacks(this)
    val backgroundScope = CoroutineScope(Dispatchers.IO)
    backgroundScope.launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(this@App) {
        Log.d(tag, "Mobile Ads initialized")
      }
    }
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    adManager.currentActivity = activity
  }

  override fun onActivityStarted(activity: Activity) {
    adManager.currentActivity = activity
  }

  override fun onActivityResumed(activity: Activity) {
    adManager.currentActivity = activity
  }

  override fun onActivityPaused(activity: Activity) {
    adManager.currentActivity = null
  }

  override fun onActivityStopped(activity: Activity) {}
  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
  override fun onActivityDestroyed(activity: Activity) {}
}