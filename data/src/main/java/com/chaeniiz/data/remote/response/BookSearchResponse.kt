package com.chaeniiz.data.remote.response

import com.google.gson.annotations.SerializedName
import com.chaeniiz.entity.Book as BookEntity

data class BookSearchResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("documents")
    val documents: List<BookDocument>
)

data class Meta(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)

data class BookDocument(
    @SerializedName("title")
    val title: String,
    @SerializedName("authors")
    val authors: List<String>,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("sale_price")
    val salePrice: Int,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("isbn")
    val isbn: String,
    @SerializedName("contents")
    val contents: String
)

fun BookDocument.toEntity(): BookEntity {
    return BookEntity(
        isbn = isbn,
        title = title,
        authors = authors,
        publisher = publisher,
        publicationDate = datetime,
        price = price,
        salePrice = salePrice,
        thumbnail = thumbnail,
        description = contents,
        isFavorite = false
    )
}
