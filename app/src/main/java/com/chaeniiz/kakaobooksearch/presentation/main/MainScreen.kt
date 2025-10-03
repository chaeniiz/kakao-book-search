package com.chaeniiz.kakaobooksearch.presentation.main

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chaeniiz.kakaobooksearch.R
import com.chaeniiz.kakaobooksearch.common.component.BottomTabBar
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreEffect
import com.chaeniiz.kakaobooksearch.presentation.shared.FavoriteStoreViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val favoriteStoreViewModel: FavoriteStoreViewModel = hiltViewModel()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val defaultError = stringResource(R.string.default_error)

    LaunchedEffect(Unit) {
        favoriteStoreViewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteStoreEffect.ShowToast -> {
                    Toast.makeText(navController.context, effect.message ?: defaultError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.BookDetail.route) {
                BottomTabBar(
                    currentRoute = currentRoute,
                    onTabClick = { route ->
                        navigateToTab(
                            navController = navController,
                            targetRoute = route,
                            currentRoute = currentRoute
                        )
                    }
                )
            }
        }
    ) { _ ->
        NavGraph(
            navController = navController,
            favoriteStoreViewModel = favoriteStoreViewModel
        )
    }
}

private fun navigateToTab(
    navController: NavController,
    targetRoute: String,
    currentRoute: String?
) {
    if (currentRoute != targetRoute) {
        navController.navigate(targetRoute) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
