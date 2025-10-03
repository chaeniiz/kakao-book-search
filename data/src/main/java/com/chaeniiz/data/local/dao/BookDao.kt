package com.chaeniiz.data.local.dao

import androidx.room.*
import com.chaeniiz.data.local.entity.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE isFavorite = 1")
    fun getFavoriteBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isbn IN (:isbns)")
    suspend fun getFavoriteBooksByIsbns(isbns: List<String>): List<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}
