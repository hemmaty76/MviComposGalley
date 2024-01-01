package com.sisapp.compose.di

import android.app.Application
import android.content.Context
import com.sisapp.compose.data.mediaStore.MediaStoreHealper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideMediaStoreHealper(@ApplicationContext context: Context): MediaStoreHealper {
        return MediaStoreHealper(context as Application)
    }

}