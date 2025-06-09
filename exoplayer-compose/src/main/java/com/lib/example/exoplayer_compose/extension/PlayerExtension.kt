package com.lib.example.exoplayer_compose.extension

import androidx.compose.ui.geometry.Size
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

fun Player.prepareVideo(url: String, seekTo: Long = 0L) {
    if (isPlaying) {
        pause()
    }
    setMediaItem(MediaItem.fromUri(url), seekTo)
    prepare()
    volume = 1f
    repeatMode = Player.REPEAT_MODE_ONE
}


fun Player.getVideoSizeDp(): Size? {
    var videoSize = Size(videoSize.width.toFloat(), videoSize.height.toFloat())
    if (videoSize.width == 0f || videoSize.height == 0f) return null

    val par = this.videoSize.pixelWidthHeightRatio
    if (par < 1.0) {
        videoSize = videoSize.copy(width = videoSize.width * par)
    } else if (par > 1.0) {
        videoSize = videoSize.copy(height = videoSize.height / par)
    }
    return videoSize
}