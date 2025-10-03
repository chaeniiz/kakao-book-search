package com.chaeniiz.kakaobooksearch.presentation.booklist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiIntent
import com.chaeniiz.entity.BookListSortType

sealed class BookListIntent : UiIntent {
    data class UpdateQuery(val query: String) : BookListIntent()
    data class ChangeSort(val sort: BookListSortType) : BookListIntent()
    data class LoadMoreBooks(val page: Int) : BookListIntent()
    data class ShowBookDetail(val book: Book) : BookListIntent()
    data class UpdateFavoriteBooks(val favoriteBooks: List<Book>) : BookListIntent()
    data object ShowSortDialog : BookListIntent()
    data object HideSortDialog : BookListIntent()
}
