package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.UiIntent
import com.chaeniiz.entity.FavoriteListSortType
import com.chaeniiz.entity.SortOrder

sealed class FavoriteListIntent : UiIntent {
    data class UpdateQuery(val query: String) : FavoriteListIntent()
    data class ChangeSort(val sortBy: FavoriteListSortType, val sortOrder: SortOrder) : FavoriteListIntent()
    data class ApplyPriceFilter(val minPrice: Int, val maxPrice: Int) : FavoriteListIntent()
    data object ClearPriceFilter : FavoriteListIntent()
    data class ShowBookDetail(val book: Book) : FavoriteListIntent()
    data class UpdateFavoriteBooks(val favoriteBooks: List<Book>) : FavoriteListIntent()
    data object ShowSortDialog : FavoriteListIntent()
    data object HideSortDialog : FavoriteListIntent()
    data object ShowFilterDialog : FavoriteListIntent()
    data object HideFilterDialog : FavoriteListIntent()
}
