package com.chaeniiz.kakaobooksearch.presentation.booklist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiEffect

sealed class BookListEffect : UiEffect {
    data class ShowToast(val message: String?) : BookListEffect()
    data class NavigateToDetail(val book: Book) : BookListEffect()
}
