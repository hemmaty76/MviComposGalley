package com.sisapp.compose.data.repository

import android.app.Application
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sisapp.compose.PAGE_SIZE
import com.sisapp.compose.data.mediaStore.MediaStoreHealper
import com.sisapp.compose.data.model.MediaModel
import com.sisapp.compose.data.pagingSource.MediaPagingSource
import com.sisapp.compose.data.toDto
import com.sisapp.compose.domin.model.MediaItem
import com.sisapp.compose.domin.reposotory.GalleryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(val mediaStore: MediaStoreHealper) :
    GalleryRepository {
    override fun getMedia(): Flow<PagingData<MediaItem>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            pagingSourceFactory = { MediaPagingSource(mediaStore) }
        ).flow.map { pagingData ->
            pagingData.map { media ->
                media.toDto()
            }
        }


}