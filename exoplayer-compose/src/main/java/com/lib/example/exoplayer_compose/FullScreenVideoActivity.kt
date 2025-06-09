package com.lib.example.exoplayer_compose

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.lib.example.exoplayer_compose.model.VideoModel
import com.lib.example.exoplayer_compose.state.rememberPlayerState


fun requestFullScreenVideo(
    context: Context,
    videoModel: VideoModel,
) {
    FullScreenVideoActivity.start(context, videoModel)
}

internal class FullScreenVideoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        setContent {
            FullScreenVideo(currentPlayingVideoModel) {
                finish()
            }

            val c = VideoModel.random("1")
            val playerState = rememberPlayerState(true, c)
            val player = playerState.player
//            PlayerView(player!!,Modifier.fillMaxWidth())
        }
    }

    companion object {
        private const val KEY_VIDEO_MODEL = "video_model"
        private lateinit var currentPlayingVideoModel: VideoModel

        internal fun start(context: Context, videoModel: VideoModel) {
            this.currentPlayingVideoModel = videoModel
            val intent = Intent(context, FullScreenVideoActivity::class.java).apply {
                putExtra(KEY_VIDEO_MODEL, videoModel)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}