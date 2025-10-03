package com.chaeniiz.kakaobooksearch.presentation.bookdetail

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiState

data class BookDetailState(
    val book: Book? = null,
    val isFavorite: Boolean = false
) : UiState
