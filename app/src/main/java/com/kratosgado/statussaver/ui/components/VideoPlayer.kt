package com.kratosgado.statussaver.ui.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
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

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(MediaItem.fromUri(uri))
      repeatMode = Player.REPEAT_MODE_ONE
      playWhenReady = false
      prepare()
    }
  }

  DisposableEffect(Unit) {
    onDispose { exoPlayer.release() }
  }

  AndroidView(
    factory = {
      PlayerView(it).apply {
        player = exoPlayer
        useController = true
        setShowNextButton(false)
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        setShowPreviousButton(false)
      }
    },
    modifier = modifier.fillMaxSize()
  )
}