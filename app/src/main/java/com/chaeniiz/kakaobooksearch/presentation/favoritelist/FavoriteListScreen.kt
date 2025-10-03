package com.chaeniiz.kakaobooksearch.presentation.favoritelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.common.component.BookCard
import com.chaeniiz.kakaobooksearch.common.component.SearchBar
import com.chaeniiz.kakaobooksearch.common.component.FilterButton
import com.chaeniiz.kakaobooksearch.common.util.FormatUtils
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteIntent
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreViewModel
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.entity.FavoriteListSortType
import com.chaeniiz.entity.SortOrder
import com.chaeniiz.kakaobooksearch.presentation.main.Screen
import kotlin.math.roundToInt
import android.widget.Toast
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteListScreen(
    navController: NavHostController,
    favoriteStoreViewModel: FavoriteStoreViewModel
) {
    val viewModel: FavoriteListViewModel = hiltViewModel()
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteBooks by favoriteStoreViewModel.favorites.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val defaultError = stringResource(R.string.default_error)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteListEffect.ShowToast -> {
                    Toast.makeText(context, effect.message ?: defaultError, Toast.LENGTH_SHORT).show()
                }

                is FavoriteListEffect.NavigateToDetail -> {
                    navController.navigate(Screen.BookDetail.createRoute(effect.book))
                }
            }
        }
    }

    LaunchedEffect(favoriteBooks) {
        viewModel.handleIntent(FavoriteListIntent.UpdateFavoriteBooks(favoriteBooks))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.favorite_list_title),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) { paddingValues ->
        FavoriteListContent(
            state = state,
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
private fun FavoriteListContent(
    state: FavoriteListState,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (FavoriteListIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = state.query,
            onQueryChange = { query ->
                onIntent(FavoriteListIntent.UpdateQuery(query))
            }
        )

        PriceFilterStatus(
            minPrice = state.minPriceFilter,
            maxPrice = state.maxPriceFilter,
            onClear = {
                onIntent(FavoriteListIntent.ClearPriceFilter)
            }
        )

        SortAndFilterOptions(
            sortBy = state.sortBy,
            sortOrder = state.sortOrder,
            onSortClick = {
                onIntent(FavoriteListIntent.ShowSortDialog)
            },
            onFilterClick = {
                onIntent(FavoriteListIntent.ShowFilterDialog)
            }
        )

        FavoriteListContent(
            state = state,
            onFavoriteClick = onFavoriteClick,
            onIntent = onIntent
        )
    }

    SortDialog(
        showDialog = state.showSortDialog,
        onDismiss = {
            onIntent(FavoriteListIntent.HideSortDialog)
        },
        onSortChange = { sortBy, sortOrder ->
            onIntent(FavoriteListIntent.ChangeSort(sortBy, sortOrder))
        }
    )

    PriceFilterDialog(
        showDialog = state.showFilterDialog,
        currentMinPrice = state.minPriceFilter,
        currentMaxPrice = state.maxPriceFilter,
        onDismiss = {
            onIntent(FavoriteListIntent.HideFilterDialog)
        },
        onApply = { minPrice, maxPrice ->
            onIntent(FavoriteListIntent.ApplyPriceFilter(minPrice, maxPrice))
        }
    )
}

@Composable
private fun PriceFilterStatus(
    minPrice: Int?,
    maxPrice: Int?,
    onClear: () -> Unit
) {
    if (minPrice != null && maxPrice != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FavoriteListConstants.HORIZONTAL_PADDING, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.favorite_list_filter_applied,
                        FormatUtils.formatPrice(minPrice),
                        FormatUtils.formatPrice(maxPrice)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                TextButton(onClick = onClear) {
                    Text(
                        text = stringResource(R.string.favorite_list_clear),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SortAndFilterOptions(
    sortBy: FavoriteListSortType,
    sortOrder: SortOrder,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FavoriteListConstants.HORIZONTAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (sortBy) {
                FavoriteListSortType.TITLE -> if (sortOrder == SortOrder.ASC) stringResource(R.string.favorite_list_title_sort_asc) else stringResource(R.string.favorite_list_title_sort_desc)
                FavoriteListSortType.PRICE -> if (sortOrder == SortOrder.ASC) stringResource(R.string.favorite_list_price_sort_asc) else stringResource(R.string.favorite_list_price_sort_desc)
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Row {
            FilterButton(
                onFilterClick = onFilterClick,
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(R.string.favorite_list_filter)
            )
            FilterButton(
                onFilterClick = onSortClick,
                text = stringResource(R.string.favorite_list_sort)
            )
        }
    }
}

@Composable
private fun FavoriteListContent(
    state: FavoriteListState,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (FavoriteListIntent) -> Unit
) {
    when {
        state.isLoading -> LoadingState()
        state.books.isEmpty() -> EmptyState(state.query.isBlank())
        else -> BookList(
            books = state.books,
            onFavoriteClick = onFavoriteClick,
            onIntent = onIntent
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(isSearchEmpty: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isSearchEmpty) {
                stringResource(R.string.favorite_list_empty_favorites)
            } else {
                stringResource(R.string.favorite_list_empty_search)
            },
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    onFavoriteClick: (Book) -> Unit,
    onIntent: (FavoriteListIntent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = maxOf(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                FavoriteListConstants.BOTTOM_PADDING
            ) + FavoriteListConstants.HORIZONTAL_PADDING
        )
    ) {
        items(books, key = { book -> book.isbn }) { book ->
            BookCard(
                book = book,
                onBookClick = {
                    onIntent(FavoriteListIntent.ShowBookDetail(book))
                },
                onFavoriteClick = { onFavoriteClick(book) }
            )
        }
    }
}

@Composable
private fun SortDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSortChange: (FavoriteListSortType, SortOrder) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.favorite_list_sort_dialog_title)) },
            text = {
                Column {
                    SortOption(
                        text = stringResource(R.string.favorite_list_sort_title_asc),
                        sortBy = FavoriteListSortType.TITLE,
                        sortOrder = SortOrder.ASC,
                        onSortChange = onSortChange,
                        onDismiss = onDismiss
                    )
                    SortOption(
                        text = stringResource(R.string.favorite_list_sort_title_desc),
                        sortBy = FavoriteListSortType.TITLE,
                        sortOrder = SortOrder.DESC,
                        onSortChange = onSortChange,
                        onDismiss = onDismiss
                    )
                    SortOption(
                        text = stringResource(R.string.favorite_list_sort_price_asc),
                        sortBy = FavoriteListSortType.PRICE,
                        sortOrder = SortOrder.ASC,
                        onSortChange = onSortChange,
                        onDismiss = onDismiss
                    )
                    SortOption(
                        text = stringResource(R.string.favorite_list_sort_price_desc),
                        sortBy = FavoriteListSortType.PRICE,
                        sortOrder = SortOrder.DESC,
                        onSortChange = onSortChange,
                        onDismiss = onDismiss
                    )
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun SortOption(
    text: String,
    sortBy: FavoriteListSortType,
    sortOrder: SortOrder,
    onSortChange: (FavoriteListSortType, SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    TextButton(
        onClick = {
            onSortChange(sortBy, sortOrder)
            onDismiss()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text)
    }
}

@Composable
private fun PriceFilterDialog(
    showDialog: Boolean,
    currentMinPrice: Int?,
    currentMaxPrice: Int?,
    onDismiss: () -> Unit,
    onApply: (Int, Int) -> Unit
) {
    var minPriceRange by remember { mutableFloatStateOf(0f) }
    var maxPriceRange by remember { mutableFloatStateOf(100000f) }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            minPriceRange = currentMinPrice?.toFloat() ?: 0f
            maxPriceRange = currentMaxPrice?.toFloat() ?: 100000f
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.favorite_list_filter_dialog_title)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(
                            R.string.favorite_list_price_range,
                            FormatUtils.formatPrice(minPriceRange.roundToInt()),
                            FormatUtils.formatPrice(maxPriceRange.roundToInt())
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    RangeSlider(
                        value = minPriceRange..maxPriceRange,
                        onValueChange = { range ->
                            minPriceRange = range.start
                            maxPriceRange = range.endInclusive
                        },
                        valueRange = 0f..100000f,
                        steps = 19, // 5000원 단위로 20개 구간
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.favorite_list_price_min),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.favorite_list_price_max),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onApply(minPriceRange.roundToInt(), maxPriceRange.roundToInt())
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.favorite_list_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.favorite_list_cancel))
                }
            }
        )
    }
}


object FavoriteListConstants {
    val BOTTOM_PADDING = 80.dp
    val HORIZONTAL_PADDING = 16.dp
}
