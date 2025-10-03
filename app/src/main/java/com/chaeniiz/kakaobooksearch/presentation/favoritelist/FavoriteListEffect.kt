package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiEffect

sealed class FavoriteListEffect : UiEffect {
    data class ShowToast(val message: String?) : FavoriteListEffect()
    data class NavigateToDetail(val book: Book) : FavoriteListEffect()
}
