package com.lib.example.exoplayer_compose

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import java.io.File

@SuppressLint("UnsafeOptInUsageError")
private var cache: SimpleCache? = null

@OptIn(UnstableApi::class)
private fun getCache(context: Context): SimpleCache {
    if (cache == null) {
        val cacheDir = File(context.cacheDir, "media-sdk")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(100L * 1024 * 1024) // 100MB max cache size
        val databaseProvider = StandaloneDatabaseProvider(context)
        cache = SimpleCache(cacheDir, cacheEvictor, databaseProvider)
    }
    return cache!!
}

@OptIn(UnstableApi::class)
private fun ExoPlayer.Builder.applyCache(context: Context) = apply {
    val cache = getCache(context)

    // Create a data source factory with cache support
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
    val cacheDataSourceFactory = CacheDataSource.Factory().setCache(cache)
        .setUpstreamDataSourceFactory(httpDataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    // Create a media source using the cache data source factory
    val mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
    setMediaSourceFactory(mediaSourceFactory)
}


internal object ExoplayerHolder {
    @Volatile
    private var player: Player? = null

    @Synchronized
    fun getOrCreatePlayer(context: Context): Player {
        return if (player == null) {
            createNewPlayer(context)
                .also {
                    player = it
                }
        } else player!!
    }

    fun createNewPlayer(context: Context): Player {
        return ExoPlayer.Builder(context)
            .applyCache(context)
            .build()
    }

    fun release(player: Player) {
        player.release()
        if (this.player == player)
            this.player = null
    }
}