package com.lib.example.exoplayer_compose.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import com.lib.example.exoplayer_compose.ExoplayerHolder
import com.lib.example.exoplayer_compose.extension.prepareVideo
import com.lib.example.exoplayer_compose.model.VideoModel

@Composable
fun rememberPlayerState(isPlaying: Boolean, videoModel: VideoModel): PlayerState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val playingFlag by rememberUpdatedState(isPlaying)
    val playerState = remember(lifecycleOwner) { PlayerState(context, { playingFlag }, videoModel, lifecycleOwner) }

    DisposableEffect(lifecycleOwner) {
        playerState.observe()
        onDispose {
            println("====>>>> player: onDispose, release ${playerState.player}")
            playerState.release()
        }
    }
    return playerState
}

class PlayerState(
    private val context: Context,
    private val isPlaying: () -> Boolean,
    private val videoModel: VideoModel,
    private val lifecycleOwner: LifecycleOwner
) {
    var player: Player? by mutableStateOf(ExoplayerHolder.createNewPlayer(context).apply {
        prepareVideo(url = videoModel.url, videoModel.seekPositionMs)
    })
        private set

    private val videoUrl = videoModel.url
    private val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                println("====>>>> player: onStart before $player , videoModel = $videoModel")
                if (player == null) {
                    player = ExoplayerHolder.createNewPlayer(context).apply {
                        prepareVideo(url = videoUrl, videoModel.seekPositionMs)
                    }
                }
                println("====>>>> player: onStart end $player , url= $videoUrl")
            }

            Lifecycle.Event.ON_PAUSE -> {
                videoModel.seekPositionMs = player?.currentPosition ?: 0
                player?.pause()
                println("====>>>> player: onPause $player , url= $videoUrl, isplaying = ${isPlaying.invoke()}, videoModel = $videoModel")
            }

            Lifecycle.Event.ON_RESUME -> {
                println("====>>>> player: onResume $player , sate = ${player?.playbackState}, isplaying = ${isPlaying.invoke()},videoModel = $videoModel")
                if (isPlaying() && player?.isPlaying == false) {
                    player?.playWhenReady = true
                }
            }

            Lifecycle.Event.ON_STOP -> {
                println("====>>>> player: onStop， release $player , url= $videoUrl, isplaying = ${isPlaying.invoke()}")
                player?.release()
                player = null
            }

            else -> {}
        }

    }

    fun observe() {
        println("===>> VideoLifecycleEffect player = $player, isPlaying = ${isPlaying.invoke()}, videoModel = $videoModel")
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    fun release() {
        player?.release()
        player = null
        lifecycleOwner.lifecycle.removeObserver(observer)
    }
}

