package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiState
import com.chaeniiz.entity.FavoriteListSortType
import com.chaeniiz.entity.SortOrder

data class FavoriteListState(
    val books: List<Book> = emptyList(),
    val originalBooks: List<Book> = emptyList(), // 필터링 전 원본 데이터
    val isLoading: Boolean = false,
    val query: String = "",
    val sortBy: FavoriteListSortType = FavoriteListSortType.TITLE,
    val sortOrder: SortOrder = SortOrder.ASC,
    val minPriceFilter: Int? = null,
    val maxPriceFilter: Int? = null,
    val showSortDialog: Boolean = false,
    val showFilterDialog: Boolean = false
) : UiState
