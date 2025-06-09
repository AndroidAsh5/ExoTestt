package com.lib.example.exoplayer_compose.model

import android.os.Parcelable
import com.lib.example.exoplayer_compose.data.videos
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class VideoModel(val url: String, val id: String = "", var seekPositionMs: Long = 0L) : Parcelable {
    companion object {
        fun random(id: String): VideoModel {
            val videoUrl = videos[Random.nextInt(videos.size)]
            return VideoModel(videoUrl, id)
        }
    }
}