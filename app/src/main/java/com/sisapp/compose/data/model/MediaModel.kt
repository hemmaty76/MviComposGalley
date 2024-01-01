package com.sisapp.compose.data.model

import android.graphics.Bitmap
import android.net.Uri

data class MediaModel(val uri: Uri,val thumbnail: Bitmap,val id:String, val type: MediaType, val dateAdded: Long = 0)

enum class MediaType {
    IMAGE,
    VIDEO
}