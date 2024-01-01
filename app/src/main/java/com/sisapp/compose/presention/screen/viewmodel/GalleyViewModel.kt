package com.sisapp.compose.presention.screen.viewmodel

import androidx.lifecycle.ViewModel
import com.sisapp.compose.domin.reposotory.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleyViewModel @Inject constructor(var galleryRepository: GalleryRepository) : ViewModel() {

    fun getListImage() = galleryRepository.getMedia()
}