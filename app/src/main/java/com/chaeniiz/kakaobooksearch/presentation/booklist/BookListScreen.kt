package com.chaeniiz.kakaobooksearch.presentation.booklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.common.component.BookCard
import com.chaeniiz.kakaobooksearch.common.component.SearchBar
import com.chaeniiz.kakaobooksearch.common.component.FilterButton
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteIntent
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreViewModel
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.entity.BookListSortType
import com.chaeniiz.kakaobooksearch.presentation.main.Screen
import android.widget.Toast
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    navController: NavHostController,
    favoriteStoreViewModel: FavoriteStoreViewModel
) {
    val viewModel: BookListViewModel = hiltViewModel()
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteBooks by favoriteStoreViewModel.favorites.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null &&
                    lastVisibleItem.index >= state.books.size - BookListConstants.PAGING_THRESHOLD &&
                    !state.isLastPage &&
                    !state.isLoading
        }
    }
    val defaultError = stringResource(R.string.default_error)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookListEffect.ShowToast -> {
                    Toast.makeText(context, effect.message ?: defaultError, Toast.LENGTH_SHORT).show()
                }

                is BookListEffect.NavigateToDetail -> {
                    navController.navigate(Screen.BookDetail.createRoute(effect.book))
                }
            }
        }
    }

    LaunchedEffect(favoriteBooks) {
        viewModel.handleIntent(BookListIntent.UpdateFavoriteBooks(favoriteBooks))
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.handleIntent(BookListIntent.LoadMoreBooks(state.currentPage + 1))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(R.string.book_list_title),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { paddingValues ->
        BookListContent(
            state = state,
            listState = listState,
            onFavoriteClick = { book ->
                favoriteStoreViewModel.handleIntent(FavoriteIntent.ToggleFavorite(book))
            },
            onIntent = { intent ->
                viewModel.handleIntent(intent)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun BookListContent(
    state: BookListState,
    listState: LazyListState,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (BookListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = state.query,
            onQueryChange = { query ->
                onIntent(BookListIntent.UpdateQuery(query))
            }
        )

        SortOptions(
            currentSort = state.sort,
            onSortClick = {
                onIntent(BookListIntent.ShowSortDialog)
            }
        )

        BookListContent(
            state = state,
            listState = listState,
            onFavoriteClick = onFavoriteClick,
            onIntent = onIntent
        )
    }

    SortDialog(
        showDialog = state.showSortDialog,
        onDismiss = {
            onIntent(BookListIntent.HideSortDialog)
        },
        onSortChange = { sort ->
            onIntent(BookListIntent.ChangeSort(sort))
        }
    )
}

@Composable
private fun SortOptions(
    currentSort: BookListSortType,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (currentSort) {
                BookListSortType.ACCURACY -> stringResource(R.string.book_list_sort_accuracy)
                BookListSortType.LATEST -> stringResource(R.string.book_list_sort_latest)
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        FilterButton(
            onFilterClick = onSortClick,
            text = stringResource(R.string.book_list_sort_button)
        )
    }
}

@Composable
private fun BookListContent(
    state: BookListState,
    listState: LazyListState,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (BookListIntent) -> Unit
) {
    when {
        state.isLoading && state.books.isEmpty() -> LoadingState()
        state.books.isEmpty() -> EmptyState()
        else -> BookList(
            books = state.books,
            isLoading = state.isLoading,
            listState = listState,
            onFavoriteClick = onFavoriteClick,
            onIntent = onIntent
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.book_list_empty_message),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    isLoading: Boolean,
    listState: LazyListState,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (BookListIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = maxOf(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                BookListConstants.BOTTOM_PADDING
            ) + 16.dp
        )
    ) {
        items(books, key = { book -> book.isbn }) { book ->
            BookCard(
                book = book,
                onBookClick = { 
                    onIntent(BookListIntent.ShowBookDetail(book))
                },
                onFavoriteClick = { onFavoriteClick(book) }
            )
        }

        if (isLoading && books.isNotEmpty()) {
            item {
                LoadingItem()
            }
        }
    }
}

@Composable
private fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SortDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSortChange: (BookListSortType) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.book_list_sort_dialog_title)) },
            text = {
                SortOptions(
                    onSortChange = onSortChange,
                    onDismiss = onDismiss
                )
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun SortOptions(
    onSortChange: (BookListSortType) -> Unit,
    onDismiss: () -> Unit
) {
    Column {
        SortOption(
            text = stringResource(R.string.book_list_sort_accuracy),
            value = BookListSortType.ACCURACY,
            onSortChange = onSortChange,
            onDismiss = onDismiss
        )
        SortOption(
            text = stringResource(R.string.book_list_sort_latest),
            value = BookListSortType.LATEST,
            onSortChange = onSortChange,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun SortOption(
    text: String,
    value: BookListSortType,
    onSortChange: (BookListSortType) -> Unit,
    onDismiss: () -> Unit
) {
    TextButton(
        onClick = {
            onSortChange(value)
            onDismiss()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}


object BookListConstants {
    val BOTTOM_PADDING = 80.dp
    const val PAGING_THRESHOLD = 5
}
