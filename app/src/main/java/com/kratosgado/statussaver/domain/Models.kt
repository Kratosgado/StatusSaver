package com.kratosgado.statussaver.domain

import android.net.Uri

sealed class Status {
  data class Image(val uri: Uri, val name: String, val saved: Boolean = false) : Status()
  data class Video(val uri: Uri, val name: String, val saved: Boolean = false) : Status()
}