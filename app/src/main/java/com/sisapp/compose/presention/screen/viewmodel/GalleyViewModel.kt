package com.sisapp.compose.presention.screen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sisapp.compose.domin.model.MediaItem
import com.sisapp.compose.domin.reposotory.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleyViewModel @Inject constructor(var galleryRepository: GalleryRepository) : ViewModel() {

    val state = MutableStateFlow<GalleyState>(GalleyState.Idle)

    fun observeListMedia() : Flow<PagingData<MediaItem>> {
         return galleryRepository.getMedia()
    }

    fun onTriggerEvent(intent: GalleyIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            when(intent){
                is GalleyIntent.GetAllMedia -> observeListMedia()
            }
        }
    }
}