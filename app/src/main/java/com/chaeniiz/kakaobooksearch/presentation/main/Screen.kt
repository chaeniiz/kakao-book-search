package com.chaeniiz.kakaobooksearch.presentation.main

import android.net.Uri
import com.chaeniiz.entity.Book
import com.google.gson.Gson

sealed class Screen(val route: String) {
    data object BookList : Screen("book_list")
    data object FavoriteList : Screen("favorite_list")
    data object BookDetail : Screen("book_detail/{book}") {
        fun createRoute(book: Book): String {
            val bookJson = Gson().toJson(book)
            return "book_detail/${Uri.encode(bookJson)}"
        }
    }
}

