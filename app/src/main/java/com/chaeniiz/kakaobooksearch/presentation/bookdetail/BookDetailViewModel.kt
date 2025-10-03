package com.chaeniiz.kakaobooksearch.presentation.bookdetail

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor() : BaseViewModel<BookDetailState, BookDetailIntent, BookDetailEffect>(
    initialState = BookDetailState()
) {
    override fun handleIntent(intent: BookDetailIntent) {
        when (intent) {
            is BookDetailIntent.SetBook -> setBook(intent.book)
            is BookDetailIntent.UpdateFavoriteStatus -> updateFavoriteStatus(intent.favoriteBooks)
            is BookDetailIntent.NavigateBack -> handleNavigateBack()
        }
    }

    private fun setBook(book: Book) {
        updateState { it.copy(book = book, isFavorite = book.isFavorite) }
    }

    private fun updateFavoriteStatus(favoriteBooks: List<Book>) {
        val currentBook = currentState.book ?: return
        val isFavorite = favoriteBooks.any { it.isbn == currentBook.isbn }
        updateState { it.copy(isFavorite = isFavorite) }
    }

    private fun handleNavigateBack() {
        sendEffect(BookDetailEffect.NavigateBack)
    }
}
