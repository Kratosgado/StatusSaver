package com.kratosgado.statussaver.utils

import android.net.Uri

sealed class Status {
  data class Image(val uri: Uri, val isSaved: Boolean) : Status()
  data class Video(val uri: Uri, val isSaved: Boolean) : Status()
}
