package com.chaeniiz.kakaobooksearch.presentation.booklist

import androidx.lifecycle.viewModelScope
import com.chaeniiz.domain.usecase.SearchBooksWithFavoriteStatusUseCase
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.base.BaseViewModel
import com.chaeniiz.entity.BookListSortType
import com.chaeniiz.entity.BookSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val searchBooksWithFavoriteStatusUseCase: SearchBooksWithFavoriteStatusUseCase
) : BaseViewModel<BookListState, BookListIntent, BookListEffect>(
    initialState = BookListState()
) {
    init {
        setupSearchFlow()
    }

    override fun handleIntent(intent: BookListIntent) {
        when (intent) {
            is BookListIntent.UpdateQuery -> updateQuery(intent.query)
            is BookListIntent.ChangeSort -> changeSort(intent.sort)
            is BookListIntent.LoadMoreBooks -> loadMore(intent.page)
            is BookListIntent.ShowBookDetail -> navigateToDetail(intent.book)
            is BookListIntent.UpdateFavoriteBooks -> updateFavoriteBooks(intent.favoriteBooks)
            is BookListIntent.ShowSortDialog -> showSortDialog()
            is BookListIntent.HideSortDialog -> hideSortDialog()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun setupSearchFlow() {
        viewModelScope.launch {
            state.map { it.query to it.sort }.distinctUntilChanged()
                .debounce(500)
                .flatMapLatest { (query, sort) ->
                    if (query.isBlank()) {
                        flow { emit(BookSearchResult.empty()) }
                    } else {
                        flow {
                            val searchResult = searchBooksWithFavoriteStatusUseCase(query, 1, sort.value)
                            emit(searchResult)
                        }
                    }
                }
                .catch { e ->
                    handleError(e)
                }
                .collect { searchResult ->
                    updateSearchResults(searchResult)
                }
        }
    }

    private fun updateQuery(query: String) {
        updateState { it.copy(query = query) }
    }

    private fun changeSort(sort: BookListSortType) {
        updateState { it.copy(sort = sort) }
    }

    private fun loadMore(page: Int) {
        if (currentState.isLastPage || currentState.isLoading) return

        viewModelScope.launch {
            try {
                setLoading(true)
                val searchResult = searchBooksWithFavoriteStatusUseCase(currentState.query, page, currentState.sort.value)
                updateLoadMoreResults(searchResult)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun navigateToDetail(book: Book) {
        sendEffect(BookListEffect.NavigateToDetail(book))
    }

    private fun setLoading(loading: Boolean) {
        updateState { it.copy(isLoading = loading) }
    }

    private fun updateSearchResults(searchResult: BookSearchResult) {
        updateState {
            it.copy(
                books = searchResult.books,
                query = currentState.query,
                currentPage = searchResult.currentPage,
                isLastPage = searchResult.isLastPage,
                isLoading = false
            )
        }
    }

    private fun updateLoadMoreResults(searchResult: BookSearchResult) {
        updateState {
            it.copy(
                books = it.books + searchResult.books,
                currentPage = searchResult.currentPage,
                isLastPage = searchResult.isLastPage,
                isLoading = false
            )
        }
    }

    private fun handleError(throwable: Throwable) {
        setLoading(false)
        sendEffect(BookListEffect.ShowToast(throwable.message))
    }

    private fun updateFavoriteBooks(favoriteBooks: List<Book>) {
        val favoriteIsbns = favoriteBooks.map { it.isbn }.toSet()
        
        updateState { currentState ->
            currentState.copy(
                books = currentState.books.map { book ->
                    book.copy(isFavorite = favoriteIsbns.contains(book.isbn))
                }
            )
        }
    }

    private fun showSortDialog() {
        updateState { it.copy(showSortDialog = true) }
    }

    private fun hideSortDialog() {
        updateState { it.copy(showSortDialog = false) }
    }
}
