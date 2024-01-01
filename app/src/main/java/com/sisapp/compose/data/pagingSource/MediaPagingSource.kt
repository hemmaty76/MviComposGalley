package com.sisapp.compose.data.pagingSource

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sisapp.compose.data.mediaStore.MediaStoreHealper
import com.sisapp.compose.data.model.MediaModel
import com.sisapp.compose.data.model.MediaType
import kotlinx.coroutines.delay

class MediaPagingSource(private val mediaStore: MediaStoreHealper) : PagingSource<Int, MediaModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaModel> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val mediaItems = mediaStore.getMixedMediaItems(page, pageSize)
            LoadResult.Page(
                data = mediaItems,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (mediaItems.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.i("TAGabs", "load: ${e.message}")
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, MediaModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }





}