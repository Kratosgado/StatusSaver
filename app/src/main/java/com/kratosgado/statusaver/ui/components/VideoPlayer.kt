package com.kratosgado.statusaver.ui.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var isPlaying = remember { mutableStateOf(false) }

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(MediaItem.fromUri(uri))
      repeatMode = Player.REPEAT_MODE_ONE
      playWhenReady = false
      prepare()
    }
  }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, e ->
      when (e) {
        Lifecycle.Event.ON_PAUSE -> {
          isPlaying.value = exoPlayer.isPlaying
          exoPlayer.pause()
        }

        Lifecycle.Event.ON_RESUME -> {
          if (isPlaying.value) exoPlayer.play()
        }

        else -> {}
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      exoPlayer.release()
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  AndroidView(
    factory = {
      PlayerView(it).apply {
        player = exoPlayer
        useController = true
        setShowNextButton(false)
        setShowPreviousButton(false)
        setShowRewindButton(false)
        setShowFastForwardButton(false)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
      }
    },
    modifier = modifier.fillMaxSize()
  )
}