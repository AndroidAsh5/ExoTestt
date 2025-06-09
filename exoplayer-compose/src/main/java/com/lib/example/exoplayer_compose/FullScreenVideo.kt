package com.lib.example.exoplayer_compose


import android.content.Context
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.lib.example.exoplayer_compose.extension.findActivity
import com.lib.example.exoplayer_compose.model.VideoModel

@OptIn(UnstableApi::class)
@Composable
fun FullScreenVideo(
    videoModel: VideoModel,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ,
        contentAlignment = Alignment.Center
    ) {
        ExoPlayerComponent(
            isPlaying = true,
            videoModel = videoModel,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight(),
        )

        Icon(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(60.dp)
                .padding(10.dp)
                .zIndex(10F)
                .clickable {
                    onDismiss()
                }
        )
    }
}

/**
 *   can not using PlayerView here, conflict with legacy Ads sdk
 * A custom [PlayerView1] that handles full-screen mode.
// */
//@OptIn(UnstableApi::class)
//@Composable
//fun PlayerView(exoplayer: Player, modifier: Modifier) {
//    AndroidView(
//        factory = { context ->
//            PlayerView(context).apply {
//                player = exoplayer
//                setShowPreviousButton(false)
//                setShowNextButton(false)
//                setFullscreenButtonClickListener { isFullScreen ->
//                    if (isFullScreen) {
//                        context.requestedOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
//                    } else {
//                        context.requestedOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                    }
//                }
//            }
//        },
//        modifier = modifier
//            .fillMaxWidth()
//            .heightIn(min = 250.dp)
//            .background(Color.Blue)
//    )
//}
//
//
//fun Context.requestedOrientation(orientation: Int) {
//    val activity = this.findActivity() ?: return
//    activity.requestedOrientation = orientation
//}