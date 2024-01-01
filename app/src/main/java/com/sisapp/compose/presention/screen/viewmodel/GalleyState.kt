package com.sisapp.compose.presention.screen.viewmodel

import androidx.paging.PagingData
import com.sisapp.compose.domin.model.MediaItem
import kotlinx.coroutines.flow.Flow

sealed class GalleyState {
    data class ReadyData(val data : Flow<PagingData<MediaItem>>) : GalleyState()
    object Idle : GalleyState()
}