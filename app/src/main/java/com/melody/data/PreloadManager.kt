package com.melody.data

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.CacheWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class PreloadManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var currentPreloadJob: Job? = null

    private val _prebufferedSongId = MutableStateFlow<String?>(null)
    val prebufferedSongId: StateFlow<String?> = _prebufferedSongId

    private val _isPrebuffering = MutableStateFlow(false)
    val isPrebuffering: StateFlow<Boolean> = _isPrebuffering

    /**
     * Pre-buffers the beginning chunk of the next song (e.g. 512KB - 1.5MB)
     * so it starts playing instantaneously without any network spin.
     */
    fun prebufferNextSong(nextSong: Song?, bytesToPreload: Long = 1024L * 1024L) {
        if (nextSong == null) return
        val path = nextSong.path
        if (!path.startsWith("http://") && !path.startsWith("https://")) {
            // Local file, already instant
            return
        }

        currentPreloadJob?.cancel()
        currentPreloadJob = scope.launch {
            try {
                _isPrebuffering.value = true
                _prebufferedSongId.value = nextSong.id

                val cache = CacheManager.getSimpleCache(context)
                val upstream = CacheManager.createUpstreamHttpDataSourceFactory().createDataSource()

                val uri = Uri.parse(path)
                val dataSpec = DataSpec.Builder()
                    .setUri(uri)
                    .setPosition(0)
                    .setLength(bytesToPreload)
                    .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                    .build()

                val cacheWriter = CacheWriter(
                    CacheManager.createCacheDataSourceFactory(context).createDataSource() as androidx.media3.datasource.cache.CacheDataSource,
                    dataSpec,
                    null
                ) { _, _, _ -> }

                cacheWriter.cache()
                _isPrebuffering.value = false
            } catch (e: Exception) {
                _isPrebuffering.value = false
            }
        }
    }

    fun isCached(song: Song): Boolean {
        if (!song.path.startsWith("http://") && !song.path.startsWith("https://")) return true
        return try {
            val cache = CacheManager.getSimpleCache(context)
            val uri = Uri.parse(song.path)
            val key = uri.toString()
            val spans = cache.getCachedSpans(key)
            spans.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun cancelPreload() {
        currentPreloadJob?.cancel()
        _isPrebuffering.value = false
    }

    companion object {
        fun buildMediaItem(song: Song): MediaItem {
            val uri = Uri.parse(song.path)
            val pathLower = song.path.lowercase()
            val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(if (!song.albumArtUri.isNullOrEmpty()) Uri.parse(song.albumArtUri) else null)
                .build()

            val mimeType = when {
                pathLower.contains(".m3u8") || pathLower.contains("hls") -> MimeTypes.APPLICATION_M3U8
                pathLower.contains(".mpd") || pathLower.contains("dash") -> MimeTypes.APPLICATION_MPD
                pathLower.contains(".mp3") -> MimeTypes.AUDIO_MPEG
                pathLower.contains(".aac") -> MimeTypes.AUDIO_AAC
                pathLower.contains(".flac") -> MimeTypes.AUDIO_FLAC
                pathLower.contains(".ogg") || pathLower.contains(".opus") -> MimeTypes.AUDIO_OGG
                else -> null
            }

            val builder = MediaItem.Builder()
                .setMediaId(song.id)
                .setUri(uri)
                .setMediaMetadata(mediaMetadata)

            if (mimeType != null) {
                builder.setMimeType(mimeType)
            }

            return builder.build()
        }
    }
}
