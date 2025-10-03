package com.chaeniiz.kakaobooksearch.presentation.shared

import androidx.lifecycle.viewModelScope
import com.chaeniiz.domain.usecase.GetFavoriteBooksUseCase
import com.chaeniiz.domain.usecase.ToggleFavoriteUseCase
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteStoreViewModel @Inject constructor(
    private val getFavoriteBooksUseCase: GetFavoriteBooksUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : BaseViewModel<FavoriteStoreState, FavoriteIntent, FavoriteStoreEffect>(
    initialState = FavoriteStoreState()
) {
    val favorites: StateFlow<List<Book>> = state
        .map { it.favorites }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        loadFavorites()
    }

    override fun handleIntent(intent: FavoriteIntent) {
        when (intent) {
            is FavoriteIntent.ToggleFavorite -> handleToggleFavorite(intent.book)
        }
    }

    private fun handleToggleFavorite(book: Book) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(book)

                val currentFavorites = currentState.favorites.toMutableList()
                val isCurrentlyFavorite = currentFavorites.any { it.isbn == book.isbn }

                if (isCurrentlyFavorite) {
                    currentFavorites.removeAll { it.isbn == book.isbn }
                } else {
                    currentFavorites.add(book.copy(isFavorite = true))
                }

                updateFavorites(currentFavorites)
            } catch (e: Exception) {
                sendEffect(FavoriteStoreEffect.ShowToast(e.message))
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        updateState { it.copy(isLoading = loading) }
    }

    private fun updateFavorites(favorites: List<Book>) {
        updateState { 
            it.copy(
                favorites = favorites,
                isLoading = false
            )
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                setLoading(true)
                val favoriteBooks = getFavoriteBooksUseCase()
                updateFavorites(favoriteBooks)
            } catch (e: Exception) {
                setLoading(false)
                sendEffect(FavoriteStoreEffect.ShowToast(e.message))
            }
        }
    }
}
