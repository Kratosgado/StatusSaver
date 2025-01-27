package com.kratosgado.statussaver.domain

import android.net.Uri

sealed class Status(val id: String, val saved: Boolean) {
  data class Image(val uri: Uri, val name: String, val isSaved: Boolean = false) :
    Status(name, isSaved)

  data class Video(val uri: Uri, val name: String, val isSaved: Boolean = false) :
    Status(name, isSaved)
}