package com.chaeniiz.kakaobooksearch.presentation.bookdetail

import com.chaeniiz.kakaobooksearch.base.UiEffect

sealed class BookDetailEffect : UiEffect {
    data class ShowToast(val message: String) : BookDetailEffect()
    data object NavigateBack : BookDetailEffect()
}
