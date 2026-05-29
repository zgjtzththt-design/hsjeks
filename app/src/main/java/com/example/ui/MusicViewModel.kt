package com.example.ui

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.MelodyApp
import com.example.data.Song
import com.example.service.PlaybackService
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
import com.example.repository.MusicRepository
import com.example.BuildConfig

import com.example.api.*
import kotlinx.serialization.json.*

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository((application as MelodyApp).database.musicDao())

    private val _onlineSearchResults = MutableStateFlow<List<MusicSearchResult>>(emptyList())
    val onlineSearchResults: StateFlow<List<MusicSearchResult>> = _onlineSearchResults

    private val _isSearchingOnline = MutableStateFlow(false)
    val isSearchingOnline: StateFlow<Boolean> = _isSearchingOnline

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

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    init {
        loadSongs()
        setupMediaController()
        startProgressUpdate()
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
                            title = cursor.getString(titleCol),
                            artist = cursor.getString(artistCol),
                            album = cursor.getString(albumCol),
                            duration = cursor.getLong(durationCol),
                            path = path,
                            albumArtUri = albumArtUri,
                            folderPath = File(path).parent ?: ""
                        )
                    )
                }
            }
            _songs.value = songList
            (getApplication() as MelodyApp).database.musicDao().insertSongs(songList)
        }
    }

    fun playSong(song: Song, albumSongs: List<Song> = emptyList()) {
        mediaController?.let { controller ->
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

    fun deletePlaylist(playlist: com.example.data.Playlist) {
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

    fun performMusicSearch(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isSearchingOnline.value = true
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val prompt = """
                    Search for music related to '$query'. 
                    Return a JSON array of objects, where each object has:
                    - 'title': The name of the song and artist.
                    - 'downloadUrl': A direct public MP3 download link (from sites like SoundHelix, Jamendo, or public archives).
                    - 'source': The source name (e.g., 'Jamendo', 'SoundHelix').
                    
                    Important: Only provide valid, direct public MP3 URLs. 
                    If you don't find a direct link, provide a representative one from SoundHelix for demonstration.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(
                        responseFormat = ResponseFormat(
                            type = "json_object",
                            text = ResponseFormatText(
                                mimeType = "application/json",
                                schema = buildJsonObject {
                                    put("type", "ARRAY")
                                    putJsonObject("items") {
                                        put("type", "OBJECT")
                                        putJsonObject("properties") {
                                            putJsonObject("title") { put("type", "STRING") }
                                            putJsonObject("downloadUrl") { put("type", "STRING") }
                                            putJsonObject("source") { put("type", "STRING") }
                                        }
                                        putJsonArray("required") {
                                            add("title")
                                            add("downloadUrl")
                                            add("source")
                                        }
                                    }
                                }
                            )
                        ),
                        temperature = 0.5f
                    )
                )

                val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (responseText != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    val results = json.decodeFromString<List<MusicSearchResult>>(responseText)
                    _onlineSearchResults.value = results
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearchingOnline.value = false
            }
        }
    }

    fun downloadSong(url: String, title: String) {
        try {
            val request = android.app.DownloadManager.Request(android.net.Uri.parse(url))
                .setTitle(title)
                .setDescription("Downloading music...")
                .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_MUSIC, "$title.mp3")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = getApplication<Application>().getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
            downloadManager.enqueue(request)
            
            android.widget.Toast.makeText(getApplication(), "Starting download: $title", android.widget.Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            android.widget.Toast.makeText(getApplication(), "Failed to start download: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun setDominantColor(color: androidx.compose.ui.graphics.Color) {
        _dominantColor.value = color
    }

    override fun onCleared() {
        super.onCleared()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
