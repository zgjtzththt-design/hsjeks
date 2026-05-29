package com.melody.repository

import com.melody.dao.MusicDao
import com.melody.data.*
import kotlinx.coroutines.flow.Flow

class MusicRepository(private val musicDao: MusicDao) {
    val mostPlayedSongs: Flow<List<SongUsage>> = musicDao.getMostPlayedSongs()
    val playlists: Flow<List<Playlist>> = musicDao.getAllPlaylists()

    suspend fun incrementPlayCount(songId: String) {
        musicDao.incrementPlayCount(songId)
    }

    suspend fun createPlaylist(name: String): Long {
        return musicDao.insertPlaylist(Playlist(name = name))
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        musicDao.deletePlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: String) {
        musicDao.addSongToPlaylist(PlaylistSong(playlistId, songId))
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String) {
        musicDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>> {
        return musicDao.getSongsInPlaylist(playlistId)
    }
}
