package com.chaeniiz.kakaobooksearch.presentation.shared

import com.chaeniiz.kakaobooksearch.base.UiEffect

sealed class FavoriteStoreEffect : UiEffect {
    data class ShowToast(val message: String?) : FavoriteStoreEffect()
}
