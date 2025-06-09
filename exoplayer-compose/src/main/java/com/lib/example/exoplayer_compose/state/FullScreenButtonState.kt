package com.lib.example.exoplayer_compose.state

import android.app.Activity
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.lib.example.exoplayer_compose.extension.findActivity

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun rememberFullScreenButtonState(): FullScreenButtonState {
    val conf = LocalConfiguration.current
    val currentActivity = LocalContext.current.findActivity()
    val isPortrait = conf.orientation == Configuration.ORIENTATION_PORTRAIT
    val fullScreenState = remember(isPortrait) { FullScreenButtonState(!isPortrait, currentActivity!!) }
    return fullScreenState
}

class FullScreenButtonState(
    isFullScreen: Boolean,
    private val currentActivity: Activity
) {

    var isFullScreen by mutableStateOf(isFullScreen)
        private set

    fun handleFullScreen() {
        if (!isFullScreen) {
            currentActivity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
        } else {
            currentActivity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
