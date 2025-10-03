package com.chaeniiz.domain.usecase

import com.chaeniiz.domain.repository.BookRepository
import com.chaeniiz.domain.repository.FavoriteRepository
import com.chaeniiz.entity.BookSearchResult
import javax.inject.Inject

class SearchBooksWithFavoriteStatusUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(query: String, page: Int, sort: String): BookSearchResult {
        // 1. 순수한 책 검색 실행
        val searchResult = bookRepository.searchBooks(query, page, sort)

        // 2. 즐겨찾기 상태 조회
        val favoriteBooks = favoriteRepository.getFavoriteBooksByIsbns(
            searchResult.books.map { it.isbn }
        )
        val favoriteIsbns = favoriteBooks.map { it.isbn }.toSet()

        // 3. 즐겨찾기 상태 병합
        val booksWithFavoriteStatus = searchResult.books.map { book ->
            book.copy(isFavorite = favoriteIsbns.contains(book.isbn))
        }

        // 4. 결과 반환
        return searchResult.copy(books = booksWithFavoriteStatus)
    }
}
