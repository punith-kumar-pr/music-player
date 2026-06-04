package com.melodyflow.player.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.melodyflow.player.data.local.db.dao.FavouriteDao
import com.melodyflow.player.data.local.db.dao.PlaylistDao
import com.melodyflow.player.data.local.db.entity.FavouriteEntity
import com.melodyflow.player.data.local.db.entity.PlaylistEntity
import com.melodyflow.player.data.local.db.entity.PlaylistSongCrossRef

@Database(
    entities = [
        FavouriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MelodyDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun playlistDao(): PlaylistDao
}
