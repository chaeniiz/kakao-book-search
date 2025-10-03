package com.chaeniiz.entity

data class Book(
    val isbn: String,
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publicationDate: String,
    val price: Int,
    val salePrice: Int,
    val thumbnail: String,
    val description: String,
    val isFavorite: Boolean = false
) {
    val effectivePrice = if (salePrice > 0) salePrice else price
}
