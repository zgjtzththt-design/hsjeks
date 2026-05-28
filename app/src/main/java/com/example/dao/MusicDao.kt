package com.example.dao

import androidx.room.*
import com.example.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM song_usage ORDER BY playCount DESC, lastPlayed DESC")
    fun getMostPlayedSongs(): Flow<List<SongUsage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsage(usage: SongUsage)

    @Query("SELECT * FROM song_usage WHERE songId = :songId")
    suspend fun getUsage(songId: String): SongUsage?

    @Transaction
    suspend fun incrementPlayCount(songId: String) {
        val current = getUsage(songId)
        if (current == null) {
            insertOrUpdateUsage(SongUsage(songId, 1))
        } else {
            insertOrUpdateUsage(current.copy(playCount = current.playCount + 1, lastPlayed = System.currentTimeMillis()))
        }
    }

    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongs(songs: List<Song>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM songs JOIN playlist_songs ON songs.id = playlist_songs.songId WHERE playlistId = :playlistId")
    fun getSongsInPlaylist(playlistId: Long): Flow<List<Song>>
}
