package com.chaeniiz.data.repository

import com.chaeniiz.data.local.dao.BookDao
import com.chaeniiz.data.local.entity.toDataModel
import com.chaeniiz.data.local.entity.toEntity
import com.chaeniiz.domain.repository.FavoriteRepository
import com.chaeniiz.entity.Book
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: BookDao
) : FavoriteRepository {

    override suspend fun getFavoriteBooks(): List<Book> {
        return dao.getFavoriteBooks().first().map { it.toEntity() }
    }

    override suspend fun getFavoriteBooksByIsbns(isbns: List<String>): List<Book> {
        return dao.getFavoriteBooksByIsbns(isbns).map { it.toEntity() }
    }

    override suspend fun toggleFavorite(book: Book) {
        val updatedBook = book.copy(isFavorite = !book.isFavorite)
        if (updatedBook.isFavorite) {
            dao.insertBook(updatedBook.toDataModel())
        } else {
            dao.deleteBook(updatedBook.toDataModel())
        }
    }
}
