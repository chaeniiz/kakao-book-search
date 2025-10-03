package com.chaeniiz.data.repository

import com.chaeniiz.data.remote.api.KakaoBookApi
import com.chaeniiz.data.remote.response.toEntity
import com.chaeniiz.domain.repository.BookRepository
import com.chaeniiz.entity.BookSearchResult
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val api: KakaoBookApi
) : BookRepository {

    override suspend fun searchBooks(query: String, page: Int, sort: String): BookSearchResult {
        val response = api.searchBooks(query = query, sort = sort, page = page)
        val books = response.documents.map { it.toEntity() }

        return BookSearchResult(
            books = books,
            isLastPage = response.meta.isEnd,
            currentPage = page,
            totalCount = response.meta.totalCount,
            pageableCount = response.meta.pageableCount
        )
    }
}
