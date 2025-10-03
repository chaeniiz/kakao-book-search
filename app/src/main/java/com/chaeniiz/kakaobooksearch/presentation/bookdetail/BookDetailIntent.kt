package com.chaeniiz.kakaobooksearch.presentation.bookdetail

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiIntent

sealed class BookDetailIntent : UiIntent {
    data class SetBook(val book: Book) : BookDetailIntent()
    data class UpdateFavoriteStatus(val favoriteBooks: List<Book>) : BookDetailIntent()
    data object NavigateBack : BookDetailIntent()
}
