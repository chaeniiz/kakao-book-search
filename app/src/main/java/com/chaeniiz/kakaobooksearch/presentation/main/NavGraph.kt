package com.chaeniiz.kakaobooksearch.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chaeniiz.kakaobooksearch.presentation.bookdetail.BookDetailScreen
import com.chaeniiz.kakaobooksearch.presentation.booklist.BookListScreen
import com.chaeniiz.kakaobooksearch.presentation.favoritelist.FavoriteListScreen
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    favoriteStoreViewModel: FavoriteStoreViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.BookList.route
    ) {
        composable(Screen.BookList.route) {
            BookListScreen(
                navController = navController,
                favoriteStoreViewModel = favoriteStoreViewModel
            )
        }

        composable(Screen.FavoriteList.route) {
            FavoriteListScreen(
                navController = navController,
                favoriteStoreViewModel = favoriteStoreViewModel
            )
        }

        composable(
            route = Screen.BookDetail.route
        ) { backStackEntry ->
            val bookJson = backStackEntry.arguments?.getString("book")
            BookDetailScreen(
                navController = navController,
                bookJson = bookJson,
                favoriteStoreViewModel = favoriteStoreViewModel
            )
        }
    }
}
