package com.melody.service

// Forced recompile
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.melody.data.CacheManager

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    companion object {
        var equalizer: android.media.audiofx.Equalizer? = null
            private set
        var bassBoost: android.media.audiofx.BassBoost? = null
            private set
        var virtualizer: android.media.audiofx.Virtualizer? = null
            private set
        var loudnessEnhancer: android.media.audiofx.LoudnessEnhancer? = null
            private set

        var equalizerEnabled: Boolean = true
            set(value) {
                field = value
                try {
                    equalizer?.enabled = value
                    bassBoost?.enabled = value
                    virtualizer?.enabled = value
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        // 5 standard normalized frequency bands (-12dB to +12dB, stored as normalized -1f..1f or dB values)
        var bandLevels: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f)
            set(value) {
                field = value
                applyBandLevelsToHardware()
            }

        var bassBoostStrength: Float = 0f // 0f..1f
            set(value) {
                field = value
                try {
                    bassBoost?.setStrength((value.coerceIn(0f, 1f) * 1000).toInt().toShort())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        var virtualizerStrength: Float = 0f // 0f..1f
            set(value) {
                field = value
                try {
                    virtualizer?.setStrength((value.coerceIn(0f, 1f) * 1000).toInt().toShort())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        var currentTargetGain: Int = 0
            set(value) {
                field = value
                try {
                    loudnessEnhancer?.setTargetGain(value)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        
        private fun applyBandLevelsToHardware() {
            try {
                equalizer?.let { eq ->
                    val minLevel = eq.bandLevelRange.getOrNull(0) ?: -1200
                    val maxLevel = eq.bandLevelRange.getOrNull(1) ?: 1200
                    val numBands = eq.numberOfBands.toInt()
                    for (i in 0 until minOf(numBands, bandLevels.size)) {
                        // bandLevels[i] is in dB (-12f to +12f)
                        val db = bandLevels[i].coerceIn(-12f, 12f)
                        // Convert dB to millibels (-1200 to +1200)
                        val milliBels = (db * 100).toInt().coerceIn(minLevel.toInt(), maxLevel.toInt()).toShort()
                        eq.setBandLevel(i.toShort(), milliBels)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        private val _amplitude = kotlinx.coroutines.flow.MutableStateFlow(0f)
        val amplitude: kotlinx.coroutines.flow.StateFlow<Float> = _amplitude

        private var visualizer: android.media.audiofx.Visualizer? = null
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val cacheDataSourceFactory = CacheManager.createCacheDataSourceFactory(this)
        val mediaSourceFactory = DefaultMediaSourceFactory(cacheDataSourceFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 60_000,
                /* bufferForPlaybackMs = */ 500, // Instant playback without waiting full file!
                /* bufferForPlaybackAfterRebufferMs = */ 1000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                if (audioSessionId != C.AUDIO_SESSION_ID_UNSET) {
                    setupAudioEffects(audioSessionId)
                }
            }
        })

        mediaSession = MediaSession.Builder(this, player).build()
    }

    private fun setupAudioEffects(audioSessionId: Int) {
        try {
            loudnessEnhancer?.release()
            loudnessEnhancer = android.media.audiofx.LoudnessEnhancer(audioSessionId).apply {
                enabled = true
                setTargetGain(currentTargetGain)
            }

            equalizer?.release()
            equalizer = android.media.audiofx.Equalizer(0, audioSessionId).apply {
                enabled = equalizerEnabled
            }
            applyBandLevelsToHardware()

            bassBoost?.release()
            bassBoost = android.media.audiofx.BassBoost(0, audioSessionId).apply {
                enabled = equalizerEnabled
                if (strengthSupported) {
                    setStrength((bassBoostStrength.coerceIn(0f, 1f) * 1000).toInt().toShort())
                }
            }

            virtualizer?.release()
            virtualizer = android.media.audiofx.Virtualizer(0, audioSessionId).apply {
                enabled = equalizerEnabled
                if (strengthSupported) {
                    setStrength((virtualizerStrength.coerceIn(0f, 1f) * 1000).toInt().toShort())
                }
            }

            visualizer?.release()
            visualizer = android.media.audiofx.Visualizer(audioSessionId).apply {
                captureSize = android.media.audiofx.Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : android.media.audiofx.Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: android.media.audiofx.Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                        if (waveform != null && waveform.isNotEmpty()) {
                            var sum = 0f
                            for (i in 0 until waveform.size) {
                                val amplitude = (waveform[i].toInt() and 0xFF) - 128
                                sum += Math.abs(amplitude).toFloat()
                            }
                            _amplitude.value = (sum / waveform.size) / 128f
                        }
                    }

                    override fun onFftDataCapture(v: android.media.audiofx.Visualizer?, fft: ByteArray?, samplingRate: Int) {}
                }, android.media.audiofx.Visualizer.getMaxCaptureRate() / 2, true, false)
                enabled = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        visualizer?.release()
        visualizer = null
        equalizer?.release()
        equalizer = null
        bassBoost?.release()
        bassBoost = null
        virtualizer?.release()
        virtualizer = null
        loudnessEnhancer?.release()
        loudnessEnhancer = null
        super.onDestroy()
    }
}
