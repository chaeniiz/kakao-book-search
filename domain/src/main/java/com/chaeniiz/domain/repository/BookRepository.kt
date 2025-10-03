package com.chaeniiz.domain.repository

import com.chaeniiz.entity.BookSearchResult

interface BookRepository {
    suspend fun searchBooks(query: String, page: Int, sort: String): BookSearchResult
}
