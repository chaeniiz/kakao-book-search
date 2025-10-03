package com.chaeniiz.kakaobooksearch.presentation.bookdetail

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.AsyncImagePainter
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.kakaobooksearch.common.util.FormatUtils
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteIntent
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreViewModel
import com.google.gson.Gson

@Composable
fun BookDetailScreen(
    navController: NavHostController,
    bookJson: String?,
    favoriteStoreViewModel: FavoriteStoreViewModel
) {
    val viewModel: BookDetailViewModel = hiltViewModel()
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favoriteBooks by favoriteStoreViewModel.favorites.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val currentBook = state.book
    val errorStr = stringResource(R.string.book_detail_book_not_found)

    LaunchedEffect(bookJson) {
        bookJson?.let { json ->
            try {
                val decodedJson = Uri.decode(json)
                val book = Gson().fromJson(decodedJson, Book::class.java)
                viewModel.handleIntent(BookDetailIntent.SetBook(book))
            } catch (e: Exception) {
                Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookDetailEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is BookDetailEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(favoriteBooks) {
        viewModel.handleIntent(BookDetailIntent.UpdateFavoriteStatus(favoriteBooks))
    }

    currentBook?.let { book ->
        Scaffold(
            topBar = {
                BookDetailTopBar(
                    isFavorite = state.isFavorite,
                    onBackClick = {
                        viewModel.handleIntent(BookDetailIntent.NavigateBack)
                    },
                    onFavoriteClick = {
                        favoriteStoreViewModel.handleIntent(FavoriteIntent.ToggleFavorite(book))
                    }
                )
            }
        ) { paddingValues ->
            BookDetailContent(
                state = state,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailTopBar(
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.book_detail_back_button)
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) stringResource(R.string.book_detail_remove_favorite) else stringResource(R.string.book_detail_add_favorite),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun BookDetailContent(
    state: BookDetailState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BookTitle(title = state.book?.title ?: "")
        BookInfo(book = state.book)
        Spacer(modifier = Modifier.height(24.dp))
        BookDescriptionSection(description = state.book?.description)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BookTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun BookInfo(book: Book?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        BookImage(
            thumbnail = book?.thumbnail ?: "",
            title = book?.title ?: ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        BookInfoDetails(book = book)
    }
}

@Composable
private fun BookImage(thumbnail: String, title: String) {
    SubcomposeAsyncImage(
        model = thumbnail,
        contentDescription = title,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    ) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is AsyncImagePainter.State.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.component_book_card_default_image),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

@Composable
private fun BookInfoDetails(book: Book?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DetailItem(stringResource(R.string.book_detail_author), book?.authors?.joinToString(", ") ?: "")
        DetailItem(stringResource(R.string.book_detail_publisher), book?.publisher ?: "")
        DetailItem(stringResource(R.string.book_detail_publication_date), FormatUtils.formatDate(book?.publicationDate ?: ""))
        DetailItem(stringResource(R.string.book_detail_isbn), book?.isbn ?: "")
        DetailItem(stringResource(R.string.book_detail_price), FormatUtils.formatPrice(book?.price ?: 0))
        DetailItem(stringResource(R.string.book_detail_sale_price), FormatUtils.formatSalePrice(book?.salePrice ?: 0, book?.price ?: 0))
    }
}

@Composable
private fun BookDescriptionSection(description: String?) {
    Text(
        text = stringResource(R.string.book_detail_description_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = description?.ifBlank { stringResource(R.string.book_detail_no_description) } ?: stringResource(R.string.book_detail_no_description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label : $value",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
