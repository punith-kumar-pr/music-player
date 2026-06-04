package com.melodyflow.player.di

import android.content.ContentResolver
import android.content.Context
import com.melodyflow.player.data.local.db.dao.FavouriteDao
import com.melodyflow.player.data.local.db.dao.PlaylistDao
import com.melodyflow.player.data.local.mediastore.MediaStoreDataSource
import com.melodyflow.player.data.repository.MusicRepository
import com.melodyflow.player.data.repository.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideMediaStoreDataSource(contentResolver: ContentResolver): MediaStoreDataSource {
        return MediaStoreDataSource(contentResolver)
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        mediaStoreDataSource: MediaStoreDataSource,
        favouriteDao: FavouriteDao,
        playlistDao: PlaylistDao
    ): MusicRepository {
        return MusicRepositoryImpl(mediaStoreDataSource, favouriteDao, playlistDao)
    }
}
