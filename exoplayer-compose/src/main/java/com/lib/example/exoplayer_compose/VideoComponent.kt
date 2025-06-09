package com.lib.example.exoplayer_compose

import android.content.Context
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import com.lib.example.exoplayer_compose.buttons.FullScreenButton
import com.lib.example.exoplayer_compose.buttons.MuteButton
import com.lib.example.exoplayer_compose.buttons.PlayPauseButton
import com.lib.example.exoplayer_compose.extension.prepareVideo
import com.lib.example.exoplayer_compose.extension.resizeWithContentScale
import com.lib.example.exoplayer_compose.model.VideoModel
import com.lib.example.exoplayer_compose.state.rememberPlayerState
import com.lib.example.exoplayer_compose.state.rememberVideoSizeState


@Composable
fun ExoPlayerComponent(
    isPlaying: Boolean,
    videoModel: VideoModel,
    modifier: Modifier = Modifier,
    thumbnails: @Composable (url: String) -> Unit = {},
    requestFullScreen: ((isFullScreen: Boolean) -> Unit)? = null,
    requestToPlay: () -> Unit = {},
) {
    val playerState = rememberPlayerState(isPlaying, videoModel)
    val player = playerState.player

    println("===>> isPlaying x = $isPlaying, videoModel = $videoModel, player = $player")

    Box(modifier = modifier) {
        BasicVideoViewInternal(
            isPlaying,
            videoModel = videoModel,
            player = player,
            modifier = modifier,
            thumbnails = thumbnails,
            requestToPlay = requestToPlay
        )
        if (isPlaying && player != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .align(Alignment.BottomEnd)
            ) {
                val modifier = Modifier
                    .size(30.dp)
                    .padding(2.dp)
                PlayPauseButton(player = player, modifier = modifier)
                MuteButton(player = player, modifier = modifier)
                FullScreenButton(requestFullScreen = requestFullScreen, modifier = modifier) {
                    videoModel.seekPositionMs = player.currentPosition
                }
            }
        }
    }
}

// show video thumbnails if not playing
@Composable
internal fun BasicVideoViewInternal(
    isPlaying: Boolean,
    videoModel: VideoModel,
    player: Player?,
    modifier: Modifier = Modifier,
    thumbnails: @Composable (url: String) -> Unit = {},
    requestToPlay: () -> Unit = {}
) {
    val videoUrl = videoModel.url
    val playWhenReady by rememberUpdatedState(isPlaying)
    LaunchedEffect(playWhenReady) {
        println("====>>>> BasicVideoViewInternal LaunchedEffect player: $player , id= ${videoModel.id}, isPlaying = $playWhenReady")
        if (playWhenReady && player != null) {
            player.prepareVideo(videoUrl, videoModel.seekPositionMs)
            player.playWhenReady = true
        } else {
            player?.pause()
        }
    }

    Box(modifier.background(color = Color.Cyan)) {
        if (playWhenReady && player != null) {
            val videoSizeState = rememberVideoSizeState(player)
            PlayerSurface(
                player = player,
                Modifier
                    .fillMaxSize()
                    .resizeWithContentScale(ContentScale.Fit, videoSizeState.videoSizeDp)
            )
        } else {
            thumbnails(videoUrl)
            Image(
                painterResource(id = R.drawable.play), contentDescription = "",
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
                    .clickable {
                        requestToPlay.invoke()
                    })
        }
    }
}


@Composable
private fun PlayerSurface(
    player: Player,
    modifier: Modifier
) {
    PlayerSurfaceInternal(
        player,
        modifier,
        createView = { SurfaceView(it) },
        setViewOnPlayer = { player, view -> player.setVideoSurfaceView(view) },
        clearViewFromPlayer = { player, view -> player.clearVideoSurfaceView(view) },
    )
}

@Composable
private fun <T : View> PlayerSurfaceInternal(
    player: Player,
    modifier: Modifier,
    createView: (Context) -> T,
    setViewOnPlayer: (Player, T) -> Unit,
    clearViewFromPlayer: (Player, T) -> Unit,
) {
    var view by remember { mutableStateOf<T?>(null) }
    var registeredPlayer by remember { mutableStateOf<Player?>(null) }
    AndroidView(factory = {
        createView(it).apply {
            view = this
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }, onReset = {}, modifier = modifier)
    view?.let { view ->
        LaunchedEffect(view, player) {
            registeredPlayer?.let { previousPlayer ->
                if (previousPlayer.isCommandAvailable(Player.COMMAND_SET_VIDEO_SURFACE))
                    clearViewFromPlayer(previousPlayer, view)
                registeredPlayer = null
            }
            if (player.isCommandAvailable(Player.COMMAND_SET_VIDEO_SURFACE)) {
                setViewOnPlayer(player, view)
                registeredPlayer = player
            }
        }
    }
}
