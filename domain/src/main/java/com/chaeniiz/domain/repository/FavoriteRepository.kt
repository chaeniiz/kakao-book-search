package com.chaeniiz.domain.repository

import com.chaeniiz.entity.Book

interface FavoriteRepository {
    suspend fun getFavoriteBooks(): List<Book>
    suspend fun getFavoriteBooksByIsbns(isbns: List<String>): List<Book>
    suspend fun toggleFavorite(book: Book)
}
