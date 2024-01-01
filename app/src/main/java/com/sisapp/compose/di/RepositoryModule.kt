package com.sisapp.compose.di

import com.sisapp.compose.data.repository.GalleryRepositoryImpl
import com.sisapp.compose.domin.reposotory.GalleryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindGalleryRepository(repository: GalleryRepositoryImpl) : GalleryRepository

}

