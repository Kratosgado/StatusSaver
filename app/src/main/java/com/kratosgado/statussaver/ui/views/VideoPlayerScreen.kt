package com.kratosgado.statussaver.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayerScreen(
  videoUri: Uri,
  onBackPressed: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val exoPlayer = remember {
    ExoPlayer.Builder(context)
      .build()
      .apply {
        setMediaItem(MediaItem.fromUri(videoUri))
        prepare()
//        play()
      }
  }

  DisposableEffect(Unit) {
    onDispose {
      exoPlayer.release() // Clean up when composable leaves
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    AndroidView(
      factory = { ctx ->
        PlayerView(ctx).apply {
          player = exoPlayer
          useController = true // Show controls
          resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }
      },
      modifier = Modifier.fillMaxSize()
    )
  }
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      when (event) {
        Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
        Lifecycle.Event.ON_RESUME -> exoPlayer.play()
        else -> {}
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }
}