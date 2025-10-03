package com.chaeniiz.data.local.entity

import com.chaeniiz.entity.Book as BookEntity

fun Book.toEntity(): BookEntity =
    BookEntity(
        isbn = isbn,
        title = title,
        authors = authors,
        publisher = publisher,
        publicationDate = publicationDate,
        price = price,
        salePrice = salePrice,
        thumbnail = thumbnail,
        description = description,
        isFavorite = isFavorite
    )

fun BookEntity.toDataModel(): Book =
    Book(
        isbn = isbn,
        title = title,
        authors = authors,
        publisher = publisher,
        publicationDate = publicationDate,
        price = price,
        salePrice = salePrice,
        thumbnail = thumbnail,
        description = description,
        isFavorite = isFavorite
    )
