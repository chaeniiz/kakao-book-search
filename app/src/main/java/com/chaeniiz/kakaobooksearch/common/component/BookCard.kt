package com.chaeniiz.kakaobooksearch.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.chaeniiz.entity.Book
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.kakaobooksearch.common.util.FormatUtils

@Composable
fun BookCard(
    book: Book,
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onBookClick(book) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        BookCardContent(
            book = book,
            onFavoriteClick = onFavoriteClick
        )
        
        BookCardPrice(
            salePrice = book.salePrice,
            price = book.price
        )
    }
}

@Composable
private fun BookCardContent(
    book: Book,
    onFavoriteClick: (Book) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        BookCardImage(
            thumbnail = book.thumbnail,
            title = book.title
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        BookCardInfo(
            book = book,
            modifier = Modifier.weight(1f)
        )
        
        BookCardFavoriteButton(
            isFavorite = book.isFavorite,
            onClick = { onFavoriteClick(book) }
        )
    }
}

@Composable
private fun BookCardImage(
    thumbnail: String,
    title: String
) {
    SubcomposeAsyncImage(
        model = thumbnail,
        contentDescription = title,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                BookCardImagePlaceholder(
                    isLoading = true
                )
            }
            is AsyncImagePainter.State.Error -> {
                BookCardImagePlaceholder(
                    isLoading = false
                )
            }
            else -> {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

@Composable
private fun BookCardImagePlaceholder(
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
                    .background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.component_book_card_default_image),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun BookCardInfo(
    book: Book,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.component_book_card_book_type),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        BookCardInfoItem(
            label = stringResource(R.string.component_book_card_publisher),
            value = book.publisher
        )

        BookCardInfoItem(
            label = stringResource(R.string.component_book_card_author),
            value = book.authors.joinToString(", "),
            maxLines = 1
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun BookCardInfoItem(
    label: String,
    value: String,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = "$label : $value",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BookCardFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) {
                stringResource(R.string.component_book_card_remove_favorite)
            } else {
                stringResource(R.string.component_book_card_add_favorite)
            },
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun BookCardPrice(
    salePrice: Int,
    price: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = FormatUtils.formatSalePrice(salePrice, price),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
