package com.sisapp.compose.presention.screen.galley

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sisapp.compose.R
import com.sisapp.compose.presention.screen.viewmodel.GalleyViewModel


private lateinit var viewModel: GalleyViewModel

@Composable
fun Gallery(navController: NavHostController) {
    viewModel = viewModel()
    Ui()
}

@Composable
fun Ui() {
    LazyVerticalGrid(columns = GridCells.Fixed(4), content = {
        items(viewModel.getListImage()) { image ->
            AsyncImage(
                modifier = Modifier.padding(12.dp).aspectRatio(1.0f).clip(RoundedCornerShape(12.dp)),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
    })
}

@Preview
@Composable
fun Preview() {
    Ui()
}

