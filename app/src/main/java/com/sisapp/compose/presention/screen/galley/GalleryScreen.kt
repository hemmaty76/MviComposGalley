package com.sisapp.compose.presention.screen.galley

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sisapp.compose.R
import com.sisapp.compose.data.model.MediaType
import com.sisapp.compose.domin.model.MediaItem
import com.sisapp.compose.presention.screen.viewmodel.GalleyViewModel


private lateinit var viewModel: GalleyViewModel

@Composable
fun GalleryScreen(navController: NavHostController) {
    viewModel = hiltViewModel()

    Ui()
}

@Composable
fun Ui() {
    val mediaPagingData = viewModel.getListImage().collectAsLazyPagingItems()
    LazyVerticalGrid(columns = GridCells.Fixed(4), content = {
        items(mediaPagingData.itemCount, key = {
            mediaPagingData[it]!!.id
        }) { index ->
            mediaPagingData[index]?.let {
                if (it.type == MediaType.VIDEO) {
                    LoadVideo(it)
                } else {
                    LoadImage(it)
                }
            }
        }
    })
}

@Composable
fun LoadVideo(it: MediaItem) {
    AsyncImage(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1.0f),
        model = ImageRequest.Builder(LocalContext.current)
            .data(it.thumbnail)
            .crossfade(true)
            .placeholder(R.drawable.placeholder)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun LoadImage(it: MediaItem) {
    val media = remember {
        mutableStateOf(it.getThumbnailFiltered())
    }
    AsyncImage(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1.0f)
            .clickable {
                media.value = it.changeFilter()
            },
        model = ImageRequest.Builder(LocalContext.current)
            .data(media.value)
            .crossfade(true)
            .placeholder(R.drawable.placeholder)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}


