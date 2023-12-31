package com.sisapp.compose.presention.screen.galley

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.service.autofill.Transformation
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.GlidePreloadingData
import com.sisapp.compose.data.model.MediaType
import com.sisapp.compose.domin.model.MediaItem
import com.sisapp.compose.presention.screen.viewmodel.GalleyViewModel
import com.sisapp.compose.presention.utils.PermissionUtils
import io.iamjosephmj.flinger.configs.FlingConfiguration
import io.iamjosephmj.flinger.flings.flingBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private lateinit var viewModel: GalleyViewModel
private lateinit var exoPlayer: ExoPlayer
private lateinit var focusItem: MutableState<MediaItem?>
private lateinit var launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>

@Composable
fun GalleryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val permissionState = remember {
        mutableStateOf(PermissionUtils.checkPermissionMedia(context))
    }
    launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissionState.value = !it.containsValue(false)
    }
    viewModel = hiltViewModel()
    focusItem = remember {
        mutableStateOf(null)
    }
    exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.playWhenReady = false
                }

                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.playWhenReady = true
                }

                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer.run {
                        stop()
                        release()
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    if (permissionState.value) {
        Ui()
    } else {
        ShowPermissionScreen()
    }
}

@Composable
fun ShowPermissionScreen() {
    Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "this app need access to your media")
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.READ_MEDIA_VIDEO))
            }else{
                launcher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }) {
            Text(text = "give access")
        }
    }
}

@Composable
fun Ui() {

    val mediaPagingData = viewModel.observeListMedia().collectAsLazyPagingItems(Dispatchers.Default)
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
//        flingBehavior = flingBehavior(
//            scrollConfiguration = FlingConfiguration.Builder()
//                .decelerationFriction(0.3f)
//                .scrollViewFriction(0.5f)
//                .splineInflection(.2f)
//                .build()
//        ),
        content = {
            items(mediaPagingData.itemCount, key = {
                mediaPagingData[it]!!.id
            }) { index ->
                mediaPagingData[index]?.let {
                    if (it.type == MediaType.VIDEO) {
                        LoadVideo(it)
                    } else {
                        LoadImageFilter(it)
                    }
                }
            }

        })

}
@OptIn(UnstableApi::class)
@Composable
fun LoadVideo(it: MediaItem) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .background(color = Color.Gray)
            .aspectRatio(1.0f)
            .clickable {
                if (focusItem.value == it) {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                } else {
                    focusItem.value = it
                }
            }
    ) {

        if (focusItem.value == it) {
            VideoPlayer(uri = it.uri)
        } else {
            LoadImage(mediaItem = it)
        }
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = null,
            modifier = Modifier
                .width(24.dp)
                .aspectRatio(1.0f)
        )
        DisposableEffect(it.id) {
            onDispose {
                if (focusItem.value == it) {
                    focusItem.value = null
                    exoPlayer.pause()
                }
            }
        }
    }
}

@Composable
@OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    exoPlayer.apply {
        val defaultDataSourceFactory = DefaultDataSource.Factory(context)
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSource.Factory(context, defaultDataSourceFactory)
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(androidx.media3.common.MediaItem.fromUri(uri))
        playWhenReady = true
        videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        repeatMode = Player.REPEAT_MODE_ONE
        setMediaSource(source)
        prepare()
    }
    AndroidView(factory = { context ->
        PlayerView(context).apply {
            hideController()
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            player = exoPlayer
        }
    })
}


@kotlin.OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoadImageFilter(mediaItem: MediaItem) {
    val media = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(mediaItem) {
        val job = coroutineScope.launch {
            delay(800)
            media.value = mediaItem.getThumbnailFiltered()
        }
        onDispose {
            job.cancel()
        }
    }

    if (media.value == null) {
        Spacer(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1.0f)
                .background(color = Color.Gray)
        )

    } else {
        GlideImage(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1.0f)
                .background(color = Color.Gray)
                .clickable {
                    media.value = mediaItem.changeFilter()

                },
            model = media.value,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }


}

@kotlin.OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoadImage(mediaItem: MediaItem) {
    val media = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(mediaItem) {
        val job = coroutineScope.launch {
            delay(800)
            media.value = mediaItem.getThumbnailFiltered()
        }
        onDispose {
            job.cancel()
        }
    }

    if (media.value == null) {
        Spacer(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1.0f)
                .background(color = Color.Gray)
        )

    } else {
        GlideImage(
            modifier = Modifier
                .padding(2.dp)
                .aspectRatio(1.0f)
                .background(color = Color.Gray),
            model = media.value,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }


}


