package com.sisapp.compose.domin.reposotory

import androidx.paging.PagingData
import com.sisapp.compose.data.model.MediaModel
import com.sisapp.compose.domin.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    fun getMedia() : Flow<PagingData<MediaItem>>
}