package com.lib.example.exoplayer_compose.buttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.lib.example.exoplayer_compose.R
import com.lib.example.exoplayer_compose.state.rememberFullScreenButtonState

@Composable
internal fun FullScreenButton(
    requestFullScreen: ((isFullScreen: Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClicked: () -> Unit = {}
) {
    val state = rememberFullScreenButtonState()
    val fullScreenIcon = if (state.isFullScreen) R.drawable.ic_fullscreen_exit_white_18dp else R.drawable.ic_fullscreen_white_18dp
    IconButton(onClick = {
        onClicked.invoke()
        if (requestFullScreen != null) {
            requestFullScreen(state.isFullScreen)
        } else {
            state.handleFullScreen()
        }
    }, modifier = modifier) {
        Icon(painterResource(fullScreenIcon), tint = Color.White, contentDescription = "", modifier = modifier)
    }
}
