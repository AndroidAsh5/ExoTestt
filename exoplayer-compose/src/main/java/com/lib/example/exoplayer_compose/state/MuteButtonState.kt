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
import androidx.media3.common.Player

@Composable
fun rememberMuteButtonState(player: Player): MuteButtonState {
    val muteButtonState = remember(player) { MuteButtonState(player) }
    LaunchedEffect(player) { muteButtonState.observe() }
    return muteButtonState
}

class MuteButtonState(private val player: Player) {
    var muted by mutableStateOf(player.volume == 0f)
        private set

    fun onClick() {
        muted = !muted
        player.volume = if (muted) 0f else 1f
    }

    fun observe() =
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                if (
                    events.containsAny(
                        Player.EVENT_DEVICE_VOLUME_CHANGED,
                        Player.EVENT_VOLUME_CHANGED,
                    )
                ) {
                   muted = player.volume == 0f
                }
            }
        })
}
