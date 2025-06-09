/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lib.example.exoplayer_compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import com.lib.example.exoplayer_compose.extension.getVideoSizeDp

@Composable
fun rememberVideoSizeState(player: Player): VideoSizeState {
    val sizeState = remember(player) { VideoSizeState(player) }
    LaunchedEffect(player) { sizeState.observe() }
    return sizeState
}

class VideoSizeState(private val player: Player) {
    var videoSizeDp: Size? by mutableStateOf(null)
        private set


    private val sizeChangeListener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            videoSizeDp = player.getVideoSizeDp()
            println("====>>>> player: onVideoSizeChanged $player , videoSizeDp= $videoSizeDp")
        }
    }

    fun observe() = player.addListener(sizeChangeListener)
}
