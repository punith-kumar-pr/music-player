package com.melodyflow.player.di

import android.content.Context
import androidx.room.Room
import com.melodyflow.player.data.local.db.MelodyDatabase
import com.melodyflow.player.data.local.db.dao.FavouriteDao
import com.melodyflow.player.data.local.db.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MelodyDatabase {
        return Room.databaseBuilder(
            context,
            MelodyDatabase::class.java,
            "melody_database"
        ).build()
    }

    @Provides
    fun provideFavouriteDao(database: MelodyDatabase): FavouriteDao {
        return database.favouriteDao()
    }

    @Provides
    fun providePlaylistDao(database: MelodyDatabase): PlaylistDao {
        return database.playlistDao()
    }
}
