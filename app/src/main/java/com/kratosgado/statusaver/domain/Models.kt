package com.kratosgado.statusaver.domain

import android.net.Uri

enum class StatusType {
  Image, Video
}

data class Status(
  val uri: Uri,
  val name: String,
  val isSaved: Boolean,
  val type: StatusType,
  val date: Long? = null
)