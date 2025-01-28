package com.kratosgado.statussaver
// File: App.kt
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {
  companion object {
    private const val LOG_TAG = "AppOpenAdManager"
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
  }

  private lateinit var appOpenAdManager: AppOpenAdManager
  private var currentActivity: Activity? = null

  override fun onCreate() {
    super.onCreate()
    registerActivityLifecycleCallbacks(this)
    val backgroundScope = CoroutineScope(Dispatchers.IO)
    backgroundScope.launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(this@App) {}
    }
    appOpenAdManager = AppOpenAdManager()
  }

  /** Interface definition for a callback to be invoked when an app open ad is complete. */
  interface OnShowAdCompleteListener {
    fun onShowAdComplete()
  }

  private inner class AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var loadTime: Long = 0;


    fun loadAd(context: Context) {
      // Do not load ad if there is an unused ad or one is already loading.
      if (isLoadingAd || isAdAvailable()) {
        return
      }

      isLoadingAd = true
      val request = AdRequest.Builder().build()
      AppOpenAd.load(
        context, AD_UNIT_ID, request,
        AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
        object : AppOpenAd.AppOpenAdLoadCallback() {

          override fun onAdLoaded(ad: AppOpenAd) {
            // Called when an app open ad has loaded.
            Log.d(LOG_TAG, "Ad was loaded.")
            appOpenAd = ad
            isLoadingAd = false
            loadTime = Date().time
          }

          override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            // Called when an app open ad has failed to load.
            Log.d(LOG_TAG, loadAdError.message)
            isLoadingAd = false;
          }
        })
    }

    /** Shows the ad if one isn't already showing. */
    fun showAdIfAvailable(
      activity: Activity,
      onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
      // If the app open ad is already showing, do not show the ad again.
      if (isShowingAd) {
        Log.d(LOG_TAG, "The app open ad is already showing.")
        return
      }

      // If the app open ad is not available yet, invoke the callback then load the ad.
      if (!isAdAvailable()) {
        Log.d(LOG_TAG, "The app open ad is not ready yet.")
        onShowAdCompleteListener.onShowAdComplete()
        loadAd(activity)
        return
      }

      appOpenAd?.setFullScreenContentCallback(
        object : FullScreenContentCallback() {

          override fun onAdDismissedFullScreenContent() {
            // Called when full screen content is dismissed.
            // Set the reference to null so isAdAvailable() returns false.
            Log.d(LOG_TAG, "Ad dismissed fullscreen content.")
            appOpenAd = null
            isShowingAd = false

            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            // Called when fullscreen content failed to show.
            // Set the reference to null so isAdAvailable() returns false.
            Log.d(LOG_TAG, adError.message)
            appOpenAd = null
            isShowingAd = false

            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
          }

          override fun onAdShowedFullScreenContent() {
            // Called when fullscreen content is shown.
            Log.d(LOG_TAG, "Ad showed fullscreen content.")
          }
        })
      isShowingAd = true
      appOpenAd?.show(activity)
    }


    /** Utility method to check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
      val dateDifference: Long = Date().time - loadTime
      val numMilliSecondsPerHour: Long = 3600000
      return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
      return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

  override fun onActivityStarted(p0: Activity) {
    currentActivity = p0
  }

  override fun onActivityResumed(p0: Activity) {}

  override fun onActivityPaused(p0: Activity) {}

  override fun onActivityStopped(p0: Activity) {}

  override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

  override fun onActivityDestroyed(p0: Activity) {}
}