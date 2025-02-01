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
  private var openAd: AppOpenAd? = null
  private var isLoadingAd = false
  private var isShowingAd = false
  private var loadTime: Long = 0

  companion object {
    private const val LOG_TAG = "AppOpenAdManager"
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
  }

  fun loadAd() {
    if (isLoadingAd || isAdAvailable()) return

    isLoadingAd = true
    val request = AdRequest.Builder().build()

    AppOpenAd.load(context, AD_UNIT_ID, request,
      AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
      object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
          openAd = ad; isLoadingAd = false; loadTime = Date().time
          Log.d(LOG_TAG, "App Open Ad Loaded")
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
          isLoadingAd = false
          Log.d(LOG_TAG, "App open Ad failed: ${p0.message}")
        }
      })
  }

  fun showAdIfAvailable(activity: Activity) {
    if (isShowingAd || !isAdAvailable()) return

    openAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
      override fun onAdDismissedFullScreenContent() {
        openAd = null; isShowingAd = false; loadAd()
      }

      override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        openAd = null; isShowingAd = false; loadAd()
        Log.d(LOG_TAG, "App open Ad failed: ${p0.message}")
      }
    }
    isShowingAd = true; openAd?.show(activity)
  }

  private fun isAdAvailable(): Boolean {
    return openAd != null && wasLoadTimeLessThanHoursAgo(4)
  }

  private fun wasLoadTimeLessThanHoursAgo(numHours: Long): Boolean {
    val dateDiff = Date().time - loadTime
    val numMilliSecPerHour: Long = 3600000
    return dateDiff < numMilliSecPerHour * numHours
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  fun onMoveToForeground() {
    if (context is Activity) {
      showAdIfAvailable(context)
    }
  }
}