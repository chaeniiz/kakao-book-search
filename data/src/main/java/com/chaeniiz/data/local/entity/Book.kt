package com.chaeniiz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chaeniiz.entity.Book as BookEntity

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
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
)
