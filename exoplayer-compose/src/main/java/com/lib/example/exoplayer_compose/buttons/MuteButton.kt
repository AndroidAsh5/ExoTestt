package com.lib.example.exoplayer_compose.buttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.media3.common.Player
import com.lib.example.exoplayer_compose.R
import com.lib.example.exoplayer_compose.state.rememberMuteButtonState

@Composable
internal fun MuteButton(player: Player, modifier: Modifier = Modifier) {
    val state = rememberMuteButtonState(player)
    val soundsIcon = if (state.muted) R.drawable.sound_off_24dp else R.drawable.sound_on_24dp
    val contentDescription =
        if (state.muted) "Mute"
        else "UnMute"
    IconButton(onClick = state::onClick, modifier = modifier) {
        Icon(painterResource(soundsIcon), tint = Color.White, contentDescription = contentDescription, modifier = modifier)
    }
}
