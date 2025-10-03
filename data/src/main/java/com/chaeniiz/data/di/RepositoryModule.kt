package com.chaeniiz.data.di

import com.chaeniiz.data.repository.BookRepositoryImpl
import com.chaeniiz.data.repository.FavoriteRepositoryImpl
import com.chaeniiz.domain.repository.BookRepository
import com.chaeniiz.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}
