package com.sisapp.compose.data

import com.sisapp.compose.data.model.MediaModel
import com.sisapp.compose.domin.model.MediaItem


fun MediaModel.toDto() : MediaItem {
    return MediaItem(this.uri,this.thumbnail,this.id,this.type)
}