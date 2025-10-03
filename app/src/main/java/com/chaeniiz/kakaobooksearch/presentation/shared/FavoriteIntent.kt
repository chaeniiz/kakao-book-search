package com.chaeniiz.kakaobooksearch.presentation.shared

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiIntent

sealed class FavoriteIntent : UiIntent {
    data class ToggleFavorite(val book: Book) : FavoriteIntent()
}
