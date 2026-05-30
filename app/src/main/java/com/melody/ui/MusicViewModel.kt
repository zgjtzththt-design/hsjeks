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

    private val audioManager by lazy { application.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val maxSysVolume by lazy { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    private val _volumeLevel = MutableStateFlow(100)
    val volumeLevel: StateFlow<Int> = _volumeLevel

    private val _showVolumeBar = MutableStateFlow(false)
    val showVolumeBar: StateFlow<Boolean> = _showVolumeBar
    private var volumeHideJob: kotlinx.coroutines.Job? = null

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = combine(_songs, repository.mostPlayedSongs) { songs, usage ->
        val usageMap = usage.associateBy({ it.songId }, { it.playCount })
        songs.sortedByDescending { usageMap[it.id] ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val playlists = repository.playlists

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackProgress = MutableStateFlow(0L)
    val playbackProgress: StateFlow<Long> = _playbackProgress

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _dominantColor = MutableStateFlow<androidx.compose.ui.graphics.Color>(androidx.compose.ui.graphics.Color.Transparent)
    val dominantColor: StateFlow<androidx.compose.ui.graphics.Color> = _dominantColor
    
    val audioAmplitude: StateFlow<Float> = PlaybackService.amplitude

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
        viewModelScope.launch {
            while (true) {
                mediaController?.let {
                    _playbackProgress.value = it.currentPosition
                    _currentDuration.value = it.duration.coerceAtLeast(0L)
                }
                kotlinx.coroutines.delay(300)
            }
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
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _currentSong.value = _songs.value.find { it.id == mediaItem?.mediaId }
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
                    "${MediaStore.Audio.Media.IS_MUSIC} != 0",
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
            val startIndex = contextSongs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            
            val mediaItems = contextSongs.map { s ->
                val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(s.title)
                    .setArtist(s.artist)
                    .setAlbumTitle(s.album)
                    .setArtworkUri(android.net.Uri.parse(s.albumArtUri ?: ""))
                    .build()

                MediaItem.Builder()
                    .setMediaId(s.id)
                    .setUri(s.path)
                    .setMediaMetadata(mediaMetadata)
                    .build()
            }

            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.prepare()
            controller.play()
            _currentSong.value = song
            viewModelScope.launch {
                repository.incrementPlayCount(song.id)
            }
        }
    }

    fun togglePlayback() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun playNext() {
        mediaController?.seekToNext()
    }

    fun playPrevious() {
        mediaController?.seekToPrevious()
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
