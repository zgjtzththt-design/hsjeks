package com.melody.ui

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.media.AudioManager
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.melody.MelodyApp
import com.melody.data.CacheManager
import com.melody.data.PreloadManager
import com.melody.data.Song
import com.melody.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.coroutines.flow.combine
import com.melody.repository.MusicRepository

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository((application as MelodyApp).database.musicDao())

    private val playbackPrefs by lazy {
        application.getSharedPreferences("playback_prefs", Context.MODE_PRIVATE)
    }

    private val audioManager by lazy { application.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val maxSysVolume by lazy { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    private val _volumeLevel = MutableStateFlow(100)
    val volumeLevel: StateFlow<Int> = _volumeLevel

    // Preload & Cache Management
    val preloadManager = PreloadManager(application)
    val isPrebuffering: StateFlow<Boolean> = preloadManager.isPrebuffering
    val prebufferedSongId: StateFlow<String?> = preloadManager.prebufferedSongId

    private val _cacheSizeMb = MutableStateFlow(0L)
    val cacheSizeMb: StateFlow<Long> = _cacheSizeMb

    private val _activeContextSongs = MutableStateFlow<List<Song>>(emptyList())
    val activeContextSongs: StateFlow<List<Song>> = _activeContextSongs

    // Crossfade State
    private val _crossfadeEnabled = MutableStateFlow(true)
    val crossfadeEnabled: StateFlow<Boolean> = _crossfadeEnabled

    private val _crossfadeDuration = MutableStateFlow(4) // Duration in seconds (1 to 12s)
    val crossfadeDuration: StateFlow<Int> = _crossfadeDuration

    private val _isCrossfading = MutableStateFlow(false)
    val isCrossfading: StateFlow<Boolean> = _isCrossfading

    private var fadeInJob: kotlinx.coroutines.Job? = null
    private var isManualFading = false
    private var isFadeInActive = false

    private val _showVolumeBar = MutableStateFlow(false)
    val showVolumeBar: StateFlow<Boolean> = _showVolumeBar
    private var volumeHideJob: kotlinx.coroutines.Job? = null

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = combine(_songs, repository.mostPlayedSongs) { songs, usage ->
        val usageMap = usage.associateBy({ it.songId }, { it.playCount })
        songs.sortedByDescending { usageMap[it.id] ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val playlists = repository.playlists
    val playbackHistory: StateFlow<List<com.melody.data.HistorySongItem>> = repository.playbackHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _isShuffleMode = MutableStateFlow(false)
    val isShuffleMode: StateFlow<Boolean> = _isShuffleMode

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _playbackProgress = MutableStateFlow(0L)
    val playbackProgress: StateFlow<Long> = _playbackProgress

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _dominantColor = MutableStateFlow<androidx.compose.ui.graphics.Color>(androidx.compose.ui.graphics.Color.Transparent)
    val dominantColor: StateFlow<androidx.compose.ui.graphics.Color> = _dominantColor
    
    val audioAmplitude: StateFlow<Float> = PlaybackService.amplitude

    private var progressJob: kotlinx.coroutines.Job? = null

    data class BluetoothDeviceRecord(
        val name: String,
        val address: String,
        val batteryLevel: Int? = null,
        val isConnected: Boolean = false
    )

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDeviceRecord>>(emptyList())
    val bluetoothDevices: StateFlow<List<BluetoothDeviceRecord>> = _bluetoothDevices

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val bluetoothReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: android.content.Intent?) {
            when (intent?.action) {
                android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED,
                android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED,
                android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED,
                "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED" -> {
                    updateBluetoothDevices()
                }
            }
        }
    }

    init {
        try {
            val currentSysVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVol = if (maxSysVolume > 0) maxSysVolume else 15
            _volumeLevel.value = ((currentSysVol.toFloat() / maxVol) * 100).toInt()
        } catch (e: Exception) {
            _volumeLevel.value = 50
        }
        loadSongs()
        setupMediaController()
        startProgressUpdate()
        
        try {
            val filter = android.content.IntentFilter().apply {
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED")
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                getApplication<Application>().registerReceiver(
                    bluetoothReceiver, 
                    filter, 
                    android.content.Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                getApplication<Application>().registerReceiver(bluetoothReceiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        updateBluetoothDevices()
    }
    
    fun updateBluetoothDevices() {
        try {
            val bluetoothManager = getApplication<Application>().getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
            val adapter = bluetoothManager?.adapter
            
            if (adapter != null) {
                try {
                    val pairedDevices = adapter.bondedDevices
                    val devices = pairedDevices?.map { device ->
                        var battery: Int? = null
                        try {
                            val method = device.javaClass.getMethod("getBatteryLevel")
                            val level = method.invoke(device) as? Int
                            if (level != null && level in 0..100) battery = level
                        } catch (e: Exception) {}

                        // Check if connected using hidden method or by checking A2DP profile
                        // For simplicity in a prototype/build, we'll use a reflection check for isConnected
                        var isConnected = false
                        try {
                            val isConnectedMethod = device.javaClass.getMethod("isConnected")
                            isConnected = isConnectedMethod.invoke(device) as? Boolean ?: false
                        } catch (e: Exception) {}

                        val deviceName = try {
                            device.name
                        } catch (e: SecurityException) {
                            "Unknown Device"
                        } ?: "Unknown Device"

                        BluetoothDeviceRecord(
                            name = deviceName,
                            address = device.address ?: "",
                            batteryLevel = battery,
                            isConnected = isConnected
                        )
                    } ?: emptyList()
                    _bluetoothDevices.value = devices
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun increaseVolume() {
        setVolumeLevel((_volumeLevel.value + 15).coerceAtMost(300))
    }

    fun decreaseVolume() {
        setVolumeLevel((_volumeLevel.value - 15).coerceAtLeast(0))
    }

    fun setVolumeLevel(level: Int) {
        _volumeLevel.value = level
        
        val sysTarget = ((level.coerceAtMost(100).toFloat() / 100) * maxSysVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sysTarget, 0)
        
        if (level > 100) {
            val boostPercent = level - 100
            val gainmB = boostPercent * 25
            PlaybackService.currentTargetGain = gainmB
        } else {
            PlaybackService.currentTargetGain = 0
        }
        
        showVolumeBarForAWhile()
    }

    private fun showVolumeBarForAWhile() {
        _showVolumeBar.value = true
        volumeHideJob?.cancel()
        volumeHideJob = viewModelScope.launch {
            kotlinx.coroutines.delay(2500)
            _showVolumeBar.value = false
        }
    }

    private fun startProgressUpdate() {
        updateProgressPolling(_isPlaying.value)
    }

    private fun updateProgressPolling(isPlaying: Boolean) {
        progressJob?.cancel()
        syncPlaybackProgress()
        if (isPlaying) {
            progressJob = viewModelScope.launch {
                while (true) {
                    syncPlaybackProgress()
                    checkAutoCrossfade()
                    kotlinx.coroutines.delay(120)
                }
            }
        } else {
            _isCrossfading.value = false
        }
    }

    private fun checkAutoCrossfade() {
        if (!_crossfadeEnabled.value || isManualFading || isFadeInActive) return
        mediaController?.let { controller ->
            val duration = controller.duration
            val currentPos = controller.currentPosition
            if (duration > 1500L && controller.isPlaying) {
                val remaining = duration - currentPos
                val fadeWindowMs = (_crossfadeDuration.value * 1000L).coerceAtMost(duration / 2)
                if (remaining in 50..fadeWindowMs) {
                    val fraction = (remaining.toFloat() / fadeWindowMs).coerceIn(0.02f, 1f)
                    val smoothVol = (1f - kotlin.math.cos(fraction * Math.PI.toFloat() / 2f)).coerceIn(0.02f, 1f)
                    controller.volume = smoothVol
                    _isCrossfading.value = true
                } else if (remaining > fadeWindowMs && controller.volume < 1f && !isFadeInActive) {
                    controller.volume = 1f
                    _isCrossfading.value = false
                }
            }
        }
    }

    private fun startSmoothFadeIn() {
        fadeInJob?.cancel()
        fadeInJob = viewModelScope.launch {
            _isCrossfading.value = true
            isFadeInActive = true
            mediaController?.volume = 0f
            val fadeDurationMs = (_crossfadeDuration.value * 1000L).coerceIn(500L, 3500L)
            val stepMs = 35L
            val steps = (fadeDurationMs / stepMs).toInt().coerceAtLeast(10)
            for (step in 1..steps) {
                val fraction = step.toFloat() / steps
                val volume = (1f - kotlin.math.cos(fraction * Math.PI.toFloat() / 2f)).coerceIn(0f, 1f)
                mediaController?.volume = volume
                kotlinx.coroutines.delay(stepMs)
            }
            mediaController?.volume = 1f
            isFadeInActive = false
            _isCrossfading.value = false
        }
    }

    private fun performManualTrackChange(block: () -> Unit) {
        if (_crossfadeEnabled.value && _isPlaying.value) {
            viewModelScope.launch {
                isManualFading = true
                _isCrossfading.value = true
                val startVol = mediaController?.volume ?: 1f
                val steps = 8
                val stepDelay = 25L // Quick ~200ms graceful fade-out before switching
                for (i in 1..steps) {
                    val vol = startVol * (1f - (i.toFloat() / steps))
                    mediaController?.volume = vol.coerceAtLeast(0f)
                    kotlinx.coroutines.delay(stepDelay)
                }
                mediaController?.volume = 0f
                block()
                isManualFading = false
                startSmoothFadeIn()
            }
        } else {
            mediaController?.volume = 1f
            _isCrossfading.value = false
            block()
        }
    }

    private fun syncPlaybackProgress() {
        mediaController?.let {
            _playbackProgress.value = it.currentPosition
            _currentDuration.value = it.duration.coerceAtLeast(0L)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun setupMediaController() {
        val sessionToken = SessionToken(getApplication(), android.content.ComponentName(getApplication(), PlaybackService::class.java))
        controllerFuture = MediaController.Builder(getApplication(), sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            mediaController?.let { controller ->
                _isPlaying.value = controller.isPlaying
                _isShuffleMode.value = controller.shuffleModeEnabled
                _repeatMode.value = controller.repeatMode
                updateProgressPolling(controller.isPlaying)
            }
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    updateProgressPolling(isPlaying)
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _isShuffleMode.value = shuffleModeEnabled
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.value = repeatMode
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val found = _songs.value.find { it.id == mediaItem?.mediaId }
                        ?: _activeContextSongs.value.find { it.id == mediaItem?.mediaId }
                    _currentSong.value = found
                    syncPlaybackProgress()
                    if (found != null) {
                        viewModelScope.launch {
                            repository.incrementPlayCount(found.id)
                        }
                        triggerNextSongPreload(found)
                    }
                    if (_crossfadeEnabled.value && _isPlaying.value && !isManualFading) {
                        startSmoothFadeIn()
                    } else if (!_crossfadeEnabled.value) {
                        mediaController?.volume = 1f
                        _isCrossfading.value = false
                    }
                    refreshCacheSize()
                }
            })
        }, MoreExecutors.directExecutor())
    }

    private fun loadSongs() {
        viewModelScope.launch {
            try {
                val songList = mutableListOf<Song>()
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.ALBUM_ID
                )

                getApplication<Application>().contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    "${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%'",
                    null,
                    null
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    while (cursor.moveToNext()) {
                        val id = cursor.getString(idCol)
                        val path = cursor.getString(dataCol)
                        val albumId = cursor.getLong(albumIdCol)
                        val albumArtUri = ContentUris.withAppendedId(
                            android.net.Uri.parse("content://media/external/audio/albumart"),
                            albumId
                        ).toString()

                        songList.add(
                            Song(
                                id = id,
                                title = cursor.getString(titleCol) ?: "Unknown Title",
                                artist = cursor.getString(artistCol) ?: "Unknown Artist",
                                album = cursor.getString(albumCol) ?: "Unknown Album",
                                duration = cursor.getLong(durationCol),
                                path = path ?: "",
                                albumArtUri = albumArtUri,
                                folderPath = if (path != null) (File(path).parent ?: "") else ""
                            )
                        )
                    }
                }
                _songs.value = songList
                (getApplication() as MelodyApp).database.musicDao().insertSongs(songList)
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSong(song: Song, albumSongs: List<Song> = emptyList()) {
        mediaController?.let { controller ->
            // If clicking the currently playing song, just pause/play
            if (_currentSong.value?.id == song.id) {
                if (controller.isPlaying) {
                    controller.pause()
                } else {
                    controller.play()
                }
                return
            }

            val contextSongs = if (albumSongs.isNotEmpty()) albumSongs else _songs.value
            _activeContextSongs.value = contextSongs
            val startIndex = contextSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            
            val mediaItems = contextSongs.map { s ->
                PreloadManager.buildMediaItem(s)
            }

            performManualTrackChange {
                controller.setMediaItems(mediaItems, startIndex, 0L)
                controller.prepare()
                controller.play()
                _currentSong.value = song
                viewModelScope.launch {
                    repository.incrementPlayCount(song.id)
                }
                triggerNextSongPreload(song, contextSongs)
            }
        }
    }

    private fun triggerNextSongPreload(current: Song, contextSongs: List<Song> = _activeContextSongs.value) {
        val list = if (contextSongs.isNotEmpty()) contextSongs else _songs.value
        val currentIndex = list.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && currentIndex + 1 < list.size) {
            val nextSong = list[currentIndex + 1]
            preloadManager.prebufferNextSong(nextSong)
        }
    }

    fun refreshCacheSize() {
        viewModelScope.launch {
            _cacheSizeMb.value = CacheManager.getCacheSizeMb(getApplication())
        }
    }

    fun clearAudioCache() {
        viewModelScope.launch {
            CacheManager.clearCache(getApplication())
            refreshCacheSize()
        }
    }

    fun togglePlayback() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun toggleShuffleMode() {
        mediaController?.let { controller ->
            val nextState = !controller.shuffleModeEnabled
            controller.shuffleModeEnabled = nextState
            _isShuffleMode.value = nextState
        }
    }

    fun toggleRepeatMode() {
        mediaController?.let { controller ->
            val nextMode = when (controller.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            controller.repeatMode = nextMode
            _repeatMode.value = nextMode
        }
    }

    fun seekTo(position: Long) {
        fadeInJob?.cancel()
        isFadeInActive = false
        isManualFading = false
        mediaController?.volume = 1f
        _isCrossfading.value = false
        mediaController?.seekTo(position)
    }

    fun playNext() {
        performManualTrackChange {
            mediaController?.seekToNext()
        }
    }

    fun playPrevious() {
        performManualTrackChange {
            mediaController?.seekToPrevious()
        }
    }

    fun setCrossfadeEnabled(enabled: Boolean) {
        _crossfadeEnabled.value = enabled
        playbackPrefs.edit().putBoolean("crossfade_enabled", enabled).apply()
        if (!enabled) {
            fadeInJob?.cancel()
            isFadeInActive = false
            isManualFading = false
            mediaController?.volume = 1f
            _isCrossfading.value = false
        }
    }

    fun setCrossfadeDuration(seconds: Int) {
        val clamped = seconds.coerceIn(1, 12)
        _crossfadeDuration.value = clamped
        playbackPrefs.edit().putInt("crossfade_duration_seconds", clamped).apply()
    }

    fun toggleCrossfade() {
        setCrossfadeEnabled(!_crossfadeEnabled.value)
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun removeSongFromHistory(songId: String) {
        viewModelScope.launch {
            repository.removeSongFromHistory(songId)
        }
    }

    fun playAllHistory(shuffle: Boolean = false) {
        val historyList = playbackHistory.value.map { it.toSong() }
        if (historyList.isNotEmpty()) {
            val listToPlay = if (shuffle) historyList.shuffled() else historyList
            playSong(listToPlay.first(), listToPlay)
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlist: com.melody.data.Playlist) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: String) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> = repository.getSongsInPlaylist(playlistId)

    fun setDominantColor(color: androidx.compose.ui.graphics.Color) {
        _dominantColor.value = color
    }

    // Equalizer & Audio Processing State
    private val _equalizerEnabled = MutableStateFlow(true)
    val equalizerEnabled: StateFlow<Boolean> = _equalizerEnabled

    private val _equalizerPresetIndex = MutableStateFlow(0)
    val equalizerPresetIndex: StateFlow<Int> = _equalizerPresetIndex

    // 5 Frequency Bands in dB (-12 dB to +12 dB) : 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz
    private val _bandLevels = MutableStateFlow(listOf(0f, 0f, 0f, 0f, 0f))
    val bandLevels: StateFlow<List<Float>> = _bandLevels

    private val _bassBoostLevel = MutableStateFlow(0.3f)
    val bassBoostLevel: StateFlow<Float> = _bassBoostLevel

    private val _virtualizerLevel = MutableStateFlow(0.2f)
    val virtualizerLevel: StateFlow<Float> = _virtualizerLevel

    fun setEqualizerEnabled(enabled: Boolean) {
        _equalizerEnabled.value = enabled
        PlaybackService.equalizerEnabled = enabled
    }

    fun setBandLevel(bandIndex: Int, levelDb: Float) {
        if (bandIndex in 0..4) {
            val updated = _bandLevels.value.toMutableList()
            updated[bandIndex] = levelDb.coerceIn(-12f, 12f)
            _bandLevels.value = updated
            PlaybackService.bandLevels = updated.toFloatArray()
        }
    }

    fun setEqualizerPreset(presetIndex: Int, bands: List<Float>, bass: Float, virtualizer: Float) {
        _equalizerPresetIndex.value = presetIndex
        _bandLevels.value = bands
        _bassBoostLevel.value = bass
        _virtualizerLevel.value = virtualizer
        PlaybackService.bandLevels = bands.toFloatArray()
        PlaybackService.bassBoostStrength = bass
        PlaybackService.virtualizerStrength = virtualizer
    }

    fun setBassBoost(level: Float) {
        val clamped = level.coerceIn(0f, 1f)
        _bassBoostLevel.value = clamped
        PlaybackService.bassBoostStrength = clamped
    }

    fun setVirtualizer(level: Float) {
        val clamped = level.coerceIn(0f, 1f)
        _virtualizerLevel.value = clamped
        PlaybackService.virtualizerStrength = clamped
    }

    fun resetEqualizer() {
        val flat = listOf(0f, 0f, 0f, 0f, 0f)
        _equalizerPresetIndex.value = 0 // Flat
        _bandLevels.value = flat
        _bassBoostLevel.value = 0f
        _virtualizerLevel.value = 0f
        PlaybackService.bandLevels = flat.toFloatArray()
        PlaybackService.bassBoostStrength = 0f
        PlaybackService.virtualizerStrength = 0f
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        try {
            getApplication<Application>().unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
