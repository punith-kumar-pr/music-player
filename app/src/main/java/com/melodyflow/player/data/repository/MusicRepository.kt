package com.melodyflow.player.data.repository

import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.local.db.entity.PlaylistEntity
import com.melodyflow.player.data.local.db.entity.PlaylistWithSongIds
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getAllSongs(): List<Song>
    fun getFavouriteIds(): Flow<List<Long>>
    fun isFavourite(songId: Long): Flow<Boolean>
    suspend fun toggleFavourite(songId: Long)
    fun getPlaylists(): Flow<List<PlaylistEntity>>
    fun getPlaylistsWithSongIds(): Flow<List<PlaylistWithSongIds>>
    suspend fun createPlaylist(name: String): Long
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun renamePlaylist(playlistId: Long, name: String)
    suspend fun addSongToPlaylist(playlistId: Long, songId: Long)
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>>
}
