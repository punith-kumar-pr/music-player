package com.melodyflow.player.data.repository

import com.melodyflow.player.data.local.db.dao.FavouriteDao
import com.melodyflow.player.data.local.db.dao.PlaylistDao
import com.melodyflow.player.data.local.db.entity.FavouriteEntity
import com.melodyflow.player.data.local.db.entity.PlaylistEntity
import com.melodyflow.player.data.local.db.entity.PlaylistSongCrossRef
import com.melodyflow.player.data.local.db.entity.PlaylistWithSongIds
import com.melodyflow.player.data.local.mediastore.MediaStoreDataSource
import com.melodyflow.player.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val favouriteDao: FavouriteDao,
    private val playlistDao: PlaylistDao
) : MusicRepository {

    override suspend fun getAllSongs(): List<Song> {
        return mediaStoreDataSource.queryAudioFiles()
    }

    override fun getFavouriteIds(): Flow<List<Long>> {
        return favouriteDao.getAllFavouriteIds()
    }

    override fun isFavourite(songId: Long): Flow<Boolean> {
        return favouriteDao.isFavourite(songId)
    }

    override suspend fun toggleFavourite(songId: Long) {
        val isFav = favouriteDao.isFavourite(songId).first()
        if (isFav) {
            favouriteDao.removeFavourite(songId)
        } else {
            favouriteDao.addFavourite(FavouriteEntity(songId = songId))
        }
    }

    override fun getPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    override fun getPlaylistsWithSongIds(): Flow<List<PlaylistWithSongIds>> {
        return playlistDao.getAllPlaylistsWithSongs()
    }

    override suspend fun createPlaylist(name: String): Long {
        return playlistDao.createPlaylist(PlaylistEntity(name = name))
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun renamePlaylist(playlistId: Long, name: String) {
        playlistDao.renamePlaylist(playlistId, name)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        playlistDao.addSongToPlaylist(PlaylistSongCrossRef(playlistId = playlistId, songId = songId))
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    override fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>> {
        return playlistDao.getSongIdsForPlaylist(playlistId)
    }
}
