package com.chaeniiz.entity

data class BookSearchResult(
    val books: List<Book>,
    val isLastPage: Boolean,
    val currentPage: Int,
    val totalCount: Int,
    val pageableCount: Int
) {
    companion object {
        fun empty() = BookSearchResult(
            books = emptyList(),
            isLastPage = true,
            currentPage = 1,
            totalCount = 0,
            pageableCount = 0
        )
    }
}
