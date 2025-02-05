package com.kratosgado.statussaver.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

class OpenAdManager(private val context: Context) : LifecycleObserver {
  private var appOpenAd: AppOpenAd? = null
  private var isLoadingAd = false
  private var isShowingAd = false
  private var loadTime: Long = 0
  var currentActivity: Activity? = null

  companion object {
    private const val LOG_TAG = "AppOpenAdManager"
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
  }

  fun loadAd() {
    if (isLoadingAd || isAdAvailable()) return

    isLoadingAd = true
    val request = AdRequest.Builder().build()

    AppOpenAd.load(
      context,
      AD_UNIT_ID,
      request,
      AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
      object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
          appOpenAd = ad
          isLoadingAd = false
          loadTime = Date().time
          Log.d("AdMob", "App Open Ad loaded")

          // Show the ad immediately after loading
//          currentActivity?.let { showAdIfAvailable(it) }
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          isLoadingAd = false
          Log.e("AdMob", "App Open Ad failed: ${loadAdError.message}")
        }
      }
    )
  }

  fun showAdIfAvailable(activity: Activity) {
    if (isShowingAd || !isAdAvailable()) return

    appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
      override fun onAdDismissedFullScreenContent() {
        appOpenAd = null
        isShowingAd = false
        loadAd()
      }

      override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        appOpenAd = null
        isShowingAd = false
        loadAd()
      }
    }

    isShowingAd = true
    appOpenAd?.show(activity)
  }

  private fun isAdAvailable(): Boolean {
    return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
  }

  private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
    val dateDifference = Date().time - loadTime
    val numMilliSecondsPerHour: Long = 3600000
    return dateDifference < numMilliSecondsPerHour * numHours
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onMoveToForeground() {
    if (!isShowingAd && isAdAvailable()) {
      currentActivity?.let { showAdIfAvailable(it) }
    }
  }
}