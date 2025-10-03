package com.chaeniiz.kakaobooksearch.presentation.booklist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiState
import com.chaeniiz.entity.BookListSortType

data class BookListState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val sort: BookListSortType = BookListSortType.ACCURACY,
    val currentPage: Int = 1,
    val isLastPage: Boolean = false,
    val showSortDialog: Boolean = false
) : UiState
