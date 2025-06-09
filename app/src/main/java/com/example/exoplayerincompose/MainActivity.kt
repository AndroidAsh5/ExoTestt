package com.example.exoplayerincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.exoplayerincompose.ui.theme.ExoplayerInComposeTheme
import com.lib.example.exoplayer_compose.ExoPlayerComponent
import com.lib.example.exoplayer_compose.model.VideoModel
import com.lib.example.exoplayer_compose.requestFullScreenVideo
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Optional
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val items = (0..100).map { index ->
            ListItem(
                index = index,
                video = if (index != 0 && index % 5 == 0) VideoModel.random(index.toString()) else null
            )
        }

//        AdsConfiguration.instance.initialise(AdsConfigurationBuilder())
        setContent {
            ExoplayerInComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoListScreen(
                        modifier = Modifier.padding(innerPadding),
                        items = items
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoListScreen(
    modifier: Modifier = Modifier,
    items: List<ListItem>
) {
    val context = LocalContext.current


    val listState = rememberLazyListState()
    var currentlyPlayingItem by remember(listState) {
        mutableStateOf(determineCurrentlyPlayingItem(listState.layoutInfo, items))
    }

    // Observe the visible item index
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .map {
                determineCurrentlyPlayingItem(it, items)
            }
            .distinctUntilChanged()
            .collect {
                currentlyPlayingItem = it
                val url = it?.run { items[it].video?.url }
                println("====>  currentlyPlayingItem = ${currentlyPlayingItem}, url =  $url")
            }
    }

    LazyColumn(state = listState, modifier = modifier.fillMaxSize()) {
        items(items.size, key = { index -> index }) { index ->
            val item = items[index]
            if (item.video != null) {
                ExoPlayerComponent(
                    isPlaying = index == currentlyPlayingItem,
                    videoModel = item.video,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    thumbnails = { videoUrl ->
                        VideoThumbnails(videoUrl)
                    },
                    requestFullScreen = {
                        requestFullScreenVideo(context = context, videoModel = item.video)
                    }
                ) {
                    currentlyPlayingItem = index
                }
            } else {
                PropertySummary(index)
            }
        }

    }
}

@Composable
fun VideoThumbnails(url: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .decoderFactory { result, options, _ ->
                VideoFrameDecoder(
                    source = result.source,
                    options = options
                )
            }
            .videoFrameMillis(3000)
            .build(),
        contentDescription = null,
        imageLoader = ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build(),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.Low
    )
}


@Composable
private fun PropertySummary(index: Int) {
    Text(
        "Property $index", modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}

private fun determineCurrentlyPlayingItem(layoutInfo: LazyListLayoutInfo, items: List<ListItem>): Int? {
    val visibleItems = layoutInfo.visibleItemsInfo.map { items[it.index] }
    val itemWithVideo = visibleItems.filter { it.video != null }
    return if (itemWithVideo.size == 1) {
        itemWithVideo.first().index
    } else {
        val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
        val itemsFromCenter =
            layoutInfo.visibleItemsInfo.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
        itemsFromCenter.map { items[it.index] }.firstOrNull() { it.video != null }?.index
    }
}

data class ListItem(
    val index: Int,
    val video: VideoModel?
)