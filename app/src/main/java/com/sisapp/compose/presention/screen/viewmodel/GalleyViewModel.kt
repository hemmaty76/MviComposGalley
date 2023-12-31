package com.sisapp.compose.presention.screen.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleyViewModel @Inject constructor() : ViewModel() {

    fun getListImage() = arrayOf(
        "https://i.stack.imgur.com/FTYpT.jpg"
    )
}