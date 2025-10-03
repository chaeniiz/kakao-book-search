package com.chaeniiz.kakaobooksearch.presentation.shared

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiState

data class FavoriteStoreState(
    val favorites: List<Book> = emptyList(),
    val isLoading: Boolean = false
) : UiState
