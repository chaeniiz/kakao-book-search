package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.BaseViewModel
import com.chaeniiz.entity.FavoriteListSortType
import com.chaeniiz.entity.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor() : BaseViewModel<FavoriteListState, FavoriteListIntent, FavoriteListEffect>(
    initialState = FavoriteListState()
) {
    override fun handleIntent(intent: FavoriteListIntent) {
        when (intent) {
            is FavoriteListIntent.UpdateQuery -> updateQuery(intent.query)
            is FavoriteListIntent.ChangeSort -> changeSort(intent.sortBy, intent.sortOrder)
            is FavoriteListIntent.ApplyPriceFilter -> applyPriceFilter(intent.minPrice, intent.maxPrice)
            is FavoriteListIntent.ClearPriceFilter -> clearPriceFilter()
            is FavoriteListIntent.ShowBookDetail -> navigateToDetail(intent.book)
            is FavoriteListIntent.UpdateFavoriteBooks -> updateFavoriteBooks(intent.favoriteBooks)
            is FavoriteListIntent.ShowSortDialog -> showSortDialog()
            is FavoriteListIntent.HideSortDialog -> hideSortDialog()
            is FavoriteListIntent.ShowFilterDialog -> showFilterDialog()
            is FavoriteListIntent.HideFilterDialog -> hideFilterDialog()
        }
    }

    private fun updateQuery(query: String) {
        updateState { it.copy(query = query) }
        refreshCurrentView()
    }

    private fun changeSort(sortBy: FavoriteListSortType, sortOrder: SortOrder) {
        updateState { it.copy(sortBy = sortBy, sortOrder = sortOrder) }
        refreshCurrentView()
    }

    private fun applyPriceFilter(minPrice: Int, maxPrice: Int) {
        updateState { it.copy(minPriceFilter = minPrice, maxPriceFilter = maxPrice) }
        refreshCurrentView()
    }

    private fun clearPriceFilter() {
        updateState { it.copy(minPriceFilter = null, maxPriceFilter = null) }
        refreshCurrentView()
    }

    private fun navigateToDetail(book: Book) {
        sendEffect(FavoriteListEffect.NavigateToDetail(book))
    }

    private fun updateFavoriteBooks(favoriteBooks: List<Book>) {
        updateState {
            it.copy(
                originalBooks = favoriteBooks,
                isLoading = false
            )
        }
        refreshCurrentView()
    }

    private fun showSortDialog() {
        updateState { it.copy(showSortDialog = true) }
    }

    private fun hideSortDialog() {
        updateState { it.copy(showSortDialog = false) }
    }

    private fun showFilterDialog() {
        updateState { it.copy(showFilterDialog = true) }
    }

    private fun hideFilterDialog() {
        updateState { it.copy(showFilterDialog = false) }
    }

    private fun refreshCurrentView() {
        val state = currentState
        
        val processedBooks = state.originalBooks
            .filter { it.matchesSearch(state.query) }
            .filter { it.matchesPriceFilter(state.minPriceFilter, state.maxPriceFilter) }
        
        updateState { 
            it.copy(books = sortBooks(processedBooks, state.sortBy, state.sortOrder))
        }
    }

    private fun sortBooks(
        books: List<Book>,
        sortBy: FavoriteListSortType,
        sortOrder: SortOrder
    ): List<Book> {
        if (books.isEmpty()) return books
        
        return when (sortBy) {
            FavoriteListSortType.TITLE -> if (sortOrder == SortOrder.ASC) {
                books.sortedBy { it.title }
            } else {
                books.sortedByDescending { it.title }
            }
            FavoriteListSortType.PRICE -> if (sortOrder == SortOrder.ASC) {
                books.sortedBy { it.effectivePrice }
            } else {
                books.sortedByDescending { it.effectivePrice }
            }
        }
    }

    private fun Book.matchesSearch(query: String): Boolean {
        if (query.isBlank()) return true
        return title.contains(query, ignoreCase = true) ||
                authors.any { it.contains(query, ignoreCase = true) } ||
                publisher.contains(query, ignoreCase = true)
    }

    private fun Book.matchesPriceFilter(minPrice: Int?, maxPrice: Int?): Boolean {
        if (minPrice == null || maxPrice == null) return true
        return effectivePrice in minPrice..maxPrice
    }
}
